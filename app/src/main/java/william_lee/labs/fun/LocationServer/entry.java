package william_lee.labs.fun.LocationServer;

/**
 * Created by william_lee on 8/22/16.
 */
public class entry {

    private String loc; //lat long
    private String  geoAddress;
    private String datetime;
    private String status; //null or SOS, fine
    public entry(String loc, String geoAddress, String datetime, String status){
        this.loc=loc;
        this.geoAddress=geoAddress;
        this.datetime=datetime;
        this.status=status;
    }
    public String getLoc(){
        return loc;
    }
    public String getGeoAddress() {
        return geoAddress;
    }
    public String getDatetime(){
        return datetime;
    }
    public String getStatus(){
        return status;
    }

    public String[] getLatLong(){
        return loc.split(" ");
    }
}
