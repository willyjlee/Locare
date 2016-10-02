package william_lee.labs.fun.LocationServer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity{

    private Button btnFine;
    private Button btnCustom;
    private Button btnRead;

    private LocationManager locationManager;

    private LocationListener gpsListen;
    private LocationListener networkListen;

    private final long minTime = 5000;

    private final float minD = 10.0f;

    private Location currentLoc;

    private final String NO_LOC = "no Location available. Turn on Location services";

    private SharedPreferences sharedp;

    private String INIT = "STARTED";

    //TODO: add in onpause/onstop unregister location thingy locationManager.removeUpdates(LocationListener)
    //maybe this

    @Override
    protected void onStop() {
        super.onStop();
        if(locationManager!=null){
            checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
            locationManager.removeUpdates(gpsListen);
            checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
            locationManager.removeUpdates(networkListen);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(!sharedp.contains("INIT")){
            //TODO: add login activity
            //TODO: add to sharedprefs
        }

        btnFine = (Button)findViewById(R.id.buttonFine);
        btnCustom = (Button)findViewById(R.id.buttonCustom);
        btnRead = (Button)findViewById(R.id.buttonRead);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        gpsListen = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLoc = location;
                //TODO: do choosing locs.
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(getApplicationContext(), "gpsStatusChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "gpsProviderEnabled", Toast.LENGTH_SHORT).show();
                Log.i("tag", "gps enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "gpsProviderDisabled", Toast.LENGTH_SHORT).show();
                Log.i("tag", "gps disabled");
            }
        };

        networkListen = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLoc = location;
                //TODO: do choosing locs.
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(getApplicationContext(), "netStatusChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "netProviderEnabled", Toast.LENGTH_SHORT).show();
                Log.i("tag", "network enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "netProviderDisabled", Toast.LENGTH_SHORT).show();
                Log.i("tag", "network disabled");
            }
        };

        btnFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLoc!=null){
                    Log.i("tag", "btn start");
                    new adder().execute("FINE", null, locationString(currentLoc));
                    Log.i("tag", "btn done");
                    Log.i("tag", locationString(currentLoc));
                }else{
                    Toast.makeText(getApplicationContext(), NO_LOC, Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: still do me
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ListActivity.class);
                Log.i("tag", "starting listactivity");
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO: tell user to turn on permissions if off
        if(locationManager!=null && gpsListen!=null && networkListen!=null){
            checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minD, gpsListen);
            checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minD, networkListen);
        }

    }

    public void showResult(int result){
        switch(result){
            case -2: Toast.makeText(getApplicationContext(), "Couldn't connect to database",Toast.LENGTH_SHORT).show(); break;
            case -1: Toast.makeText(getApplicationContext(), "Couldn't select database",Toast.LENGTH_SHORT).show(); break;
            case 0: Toast.makeText(getApplicationContext(), "Couldn't add entry",Toast.LENGTH_SHORT).show(); break;
            case 1: Toast.makeText(getApplicationContext(), "Added successful",Toast.LENGTH_SHORT).show(); break;
        }
    }

    class adder extends AsyncTask<String, Void, String> {
        private static final String addURL = "http://45.79.108.155/sdhacks/add.php";

        private int addResult;

        @Override
        protected String doInBackground(String... params) { //means add
            // params[0]=kind
            // params[1]=null or custring
            // params[2]=location
                try {
                    URL url = new URL(addURL);
                    HttpURLConnection con = (HttpURLConnection) (url.openConnection());
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.connect();
                    //TODO: add if can't even connect to url
                    JSONObject add = new JSONObject();
                    try {
                        add.put("location", params[2]);
                        //add.put("datetime", SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                        Calendar c = Calendar.getInstance();
                        add.put("date", c.get(Calendar.MONTH)+"/"+c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.YEAR));
                        add.put("time", c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+" "+(Calendar.AM_PM==Calendar.PM ? "PM" : "AM"));
                        add.put("username", params[0].equals("custom") ? params[1] : params[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                    out.write(add.toString());
                    Log.i("tag", add.toString());
                    //not sure
                    out.flush();
                    out.close();

                    BufferedReader resIn = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder build = new StringBuilder();
                    String s;
                    while((s=resIn.readLine())!=null){
                        build.append(s);
                    }

                    Log.i("tag", "Build.TOSTRING: "+build.toString());

                    try{
                        JSONObject resobj = new JSONObject(build.toString());
                        //TODO: check if stringbuilder is error message.

                        if(!resobj.has("result")){
                            Log.i("tag", "result of add has no result");
                        }else{
                            Log.i("tag", "FOUND RESULT");
                            addResult = resobj.getInt("result");
                            Log.i("tag", "Result: " + addResult);

                        }
                    }
                    catch(JSONException e){
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
                    showResult(addResult);
                }
            });
        }
    }


    public String locationString (Location l){
        return l.getLatitude()+" "+l.getLongitude();
    }


}
