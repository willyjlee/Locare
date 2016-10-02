package william_lee.labs.fun.LocationServer;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by william_lee on 8/22/16.
 */
public class Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<entry> entries;
    private LayoutInflater layoutInflater;

    public Adapter (Context context, ArrayList<entry>entries) {
        this.context = context;
        this.entries = entries;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        entry entry = entries.get(position);
        View view = layoutInflater.inflate(R.layout.entry_view, null);

        TextView loc = (TextView) view.findViewById(R.id.locView);
        TextView datetime = (TextView) view.findViewById(R.id.datetimeView);
        TextView username = (TextView) view.findViewById(R.id.usernameView);

        String geoAddr=entry.getGeoAddress();
        loc.setText(geoAddr==null||geoAddr.length()==0 ? entry.getLoc() : geoAddr);

        datetime.setText(entry.getDate() + " " +entry.getTime());

        String sta = entry.getUsername();
        username.setText(sta);
        return view;
    }

}
