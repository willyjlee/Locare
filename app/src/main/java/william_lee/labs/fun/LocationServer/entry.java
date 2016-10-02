package william_lee.labs.fun.LocationServer;

/**
 * Created by william_lee on 8/22/16.
 */
public class entry {

    private String loc; //lat long
    private String  geoAddress;
    private String date;
    private String time;
    private String username;
    public entry(String loc, String geoAddress, String date, String time, String username){
        this.loc=loc;
        this.geoAddress=geoAddress;
        this.date=date;
        this.time=time;
        this.username=username;
    }
    public String getLoc(){
        return loc;
    }
    public String getGeoAddress() {
        return geoAddress;
    }
    public String getDate(){
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getUsername(){
        return username;
    }

    public String[] getLatLong(){
        return loc.split(" ");
    }
}
