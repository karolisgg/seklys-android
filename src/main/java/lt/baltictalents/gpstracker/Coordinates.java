package lt.baltictalents.gpstracker;

import java.util.Date;

public class Coordinates {

    private int id;

    private double latitude;

    private double longitude;

    private Device device;

    private Date dateTime;

    public Coordinates() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
