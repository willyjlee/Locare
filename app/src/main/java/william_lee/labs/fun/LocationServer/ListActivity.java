package william_lee.labs.fun.LocationServer;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by william_lee on 8/22/16.
 */

//this gets from db to display in user format.
//db can be different
public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private Adapter adap;
    private ArrayList<entry> all;
    private SwipeRefreshLayout srl;
    private boolean updating;
    private boolean swiped;
    private boolean listclickable;

    private RelativeLayout progcirc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        updating = false;
        swiped = false;
        listclickable=true;
        Log.i("tag", "entered listactivity oncreate");
        listView = (ListView) findViewById(R.id.listView);
        progcirc = (RelativeLayout)findViewById(R.id.loadingCircle);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listclickable) {
                    entry clicked = (entry) adap.getItem(position);
                    Toast.makeText(getApplicationContext(), clicked.getLoc(), Toast.LENGTH_LONG).show();
                    //TODO: add g00gle maps
                    Intent i = new Intent(ListActivity.this, MapActivity.class);

                    String[]arr=clicked.getLatLong();
                    i=i.putExtra("lat", Double.parseDouble(arr[0]));
                    i=i.putExtra("long", Double.parseDouble(arr[1]));
                    i=i.putExtra("geoad", clicked.getGeoAddress());
                    i=i.putExtra("date", clicked.getDate());
                    i=i.putExtra("time", clicked.getTime());
                    i=i.putExtra("username", clicked.getUsername());

                    startActivity(i);
                }
            }
        });


        srl = (SwipeRefreshLayout) findViewById(R.id.list_swipe_refresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!updating) {
                    swiped = true;
                    update();
                } else {
                    srl.setRefreshing(false);
                }
            }
        });

        //update();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //updateAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODOne: start loading circle if not swiped
        if (!updating) {
            update();
        }
        //TODOne: set to invisible circle: http://stackoverflow.com/questions/5442183/using-the-animated-circle-in-an-imageview-while-loading-stuff

    }

    public void update() {
        updating = true;
        listclickable=false;
        if(!swiped){
            progcirc.setVisibility(View.VISIBLE);
        }
        all = new ArrayList<>();

        Log.i("tag", "entered loadArr");
        new getter().execute();

    }

    public void updateAdapter() {
        adap = new Adapter(ListActivity.this, all);
        listView.setAdapter(adap);
    }

    public void showResult(int result){
        switch(result){
            case -2: Toast.makeText(getApplicationContext(), "Couldn't connect to database",Toast.LENGTH_SHORT).show(); break;
            case -1: Toast.makeText(getApplicationContext(), "Couldn't select database",Toast.LENGTH_SHORT).show(); break;
            case 0: Toast.makeText(getApplicationContext(), "Database is empty",Toast.LENGTH_SHORT).show(); break;
            case 1: Toast.makeText(getApplicationContext(), "Retrieved successful",Toast.LENGTH_SHORT).show(); break;
        }
    }

    class getter extends AsyncTask<Void, Void, String> {

        private static final String getURL = "http://104.198.55.164/getall.php";

        private int getResult;

        private String getAddress(String locstr) { //locstr == "lat long"
            String address = "";
            if (locstr != null && locstr.length() > 0) {
                Locale locale = Locale.getDefault();
                Geocoder gc = new Geocoder(ListActivity.this, locale);
                StringTokenizer st = new StringTokenizer(locstr);//lat long
                try {
                    List<Address> adlist = gc.getFromLocation(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), 1);
                    if (adlist.size() > 0) {
                        address = adlist.get(0).getLocality();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return address;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(getURL);
                HttpURLConnection con = (HttpURLConnection) (url.openConnection());
                con.setDoInput(true);
                con.setDoOutput(false);
                con.setRequestMethod("GET");
                con.connect();
                //TODO: add if can't connect to url

                //$resp['entries'] php
                StringBuilder sb = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }
                try {
                    Log.i("tag", "JSON STRING: " + sb.toString());

                    JSONObject obj = new JSONObject(sb.toString());
                    //TODO: check if error happened where sb is error message


                    ArrayList<entry> rev = new ArrayList<>();
                    if (obj.has("entries")) {
                        JSONArray arr = obj.getJSONArray("entries");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject j = arr.getJSONObject(i);
                            String locstr = j.getString("location");
                            rev.add(new entry(locstr, getAddress(locstr), j.getString("date"), j.getString("time"), j.getString("username")));
                            Log.i("all array", j.getString("location") + " " + j.getString("date") +j.getString("time")+ " " + j.getString("username"));
                        }
                        for (int ind = rev.size() - 1; ind >= 0; ind--) {
                            all.add(rev.get(ind));
                        }
                    }

                    if(obj.has("result")){
                        getResult = obj.getInt("result");
                        Log.i("tag", "Has Result: "+getResult);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showResult(getResult);
                    updateAdapter();
                    if (swiped) {
                        srl.setRefreshing(false);
                        swiped = false;
                    }else{
                        progcirc.setVisibility(View.GONE);
                    }
                    updating = false;
                    listclickable=true;
                }
            });
        }
    }
}



