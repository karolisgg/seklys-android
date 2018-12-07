package lt.baltictalents.gpstracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONObject;

public class GPSActivity extends AppCompatActivity {

    private final GPSActivity sitas = this;
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
    }

    @Override
    protected void onResume(){
        super.onResume();
        getCurrentLocation();
    }

    public void atgal(View v) {
        finish();
    }

    public void getCurrentLocation(){
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView longitude=findViewById(R.id.longitude);
                longitude.setText(" "+location.getLongitude());
                TextView latitude=findViewById(R.id.latitude);
                latitude.setText(" "+location.getLatitude());

                Device dev = new Device();
                String androidId = Settings.Secure.getString(sitas.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                dev.setDeviceId(androidId);

                Coordinates coord = new Coordinates();
                coord.setDevice(dev);
                coord.setLatitude(location.getLatitude());
                coord.setLongitude(location.getLongitude());

                String json = new Gson().toJson(coord);
                AsyncHttpClient client = new AsyncHttpClient();
                StringEntity body = new StringEntity(json, ContentType.APPLICATION_JSON);

                client.post(sitas, "http://80.208.229.222:8080/Seklys/api/set_coordinates", body, "application/json", new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("Klaida: ", "Succ");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d("Klaida: ", "Failure");
                    }
                });
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Klaida","Nera priejimo");

            }
        };


        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, 10000,50, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }

    }
}