package lt.baltictalents.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends AppCompatActivity {

   private String androidId;
   private final SigninActivity sitas = this;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signin);
      androidId = Settings.Secure.getString(
            this.getContentResolver(),
            Settings.Secure.ANDROID_ID);
   }

   @Override
   protected void onStart() {
      super.onStart();

      AsyncHttpClient client = new AsyncHttpClient();
      RequestParams params = new RequestParams();

      params.put("deviceId", androidId);

      client.get("http://80.208.229.222:8080/Seklys/api/get_device", params, new JsonHttpResponseHandler() {

         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Intent intent = new Intent(sitas, GPSActivity.class);
            startActivity(intent);
            finish();
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            try {
               Log.d("Klaida: ", statusCode + ": " + errorResponse.getString("message"));
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }
      });
   }

   public void prisijungti(View v) {

      String name = ((EditText) findViewById(R.id.inputName)).getText().toString();
      if (name.trim().length() < 3) {
         Toast.makeText(getApplicationContext(), "Vardas turi būti iš 3 ar daugiau simbolių", Toast.LENGTH_LONG).show();
         return;
      }
      Device dev = new Device();
      dev.setDeviceId(androidId);
      dev.setName(name);

      String json = new Gson().toJson(dev);
      AsyncHttpClient client = new AsyncHttpClient();
      StringEntity body = new StringEntity(json, ContentType.APPLICATION_JSON);

      client.post(this, "http://80.208.229.222:8080/Seklys/api/add_device", body, "application/json",
            new JsonHttpResponseHandler() {

         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("Klaida: ", "Success");
            Intent intent = new Intent(sitas, GPSActivity.class);
            startActivity(intent);
            finish();
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("Klaida: ", "Failure");
            if (statusCode == 409) {
               Toast.makeText(getApplicationContext(), "Vardas jau užimtas", Toast.LENGTH_LONG).show();
            }
         }
      });
   }
}
