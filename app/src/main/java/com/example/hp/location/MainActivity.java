package com.example.hp.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.List;
public class MainActivity extends Activity implements SensorEventListener {

    private static final String TODO = "";
    SensorManager sm;
    TextView text, tv;
    long lastUpdate;
    float lastX = 0, lastY = 0, lastZ = 0;
    private GoogleApiClient client;
    private TrackGPS gps ;
    private Location location;
   private boolean ok = false;
    private int i =0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.textView1);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;
        String texte;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        long actualTime = System.currentTimeMillis();

        i++;

        if (((x - lastX) > 20) || ((y - lastY) > 20) || ((z - lastZ) > 20)) {
            ok = true;
        }


        if ((ok == true)&&(i==1)) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+21622908800", null, getAddress(), null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

        }
        lastX = x;
        lastY = y;
        lastZ = z;
    }


    public String getAddress() throws IOException {

        gps = new TrackGPS(MainActivity.this);
        if (gps.canGetLocation()) {
            double longitude = gps.getLongitude();
            double latitude = gps.getLatitude();
            String s1 = latitude + "\n" + longitude;
            String cityName = null;
            Geocoder geo = new Geocoder(MainActivity.this);
            List<Address> addresses;


            addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
            }
            else {
                if (addresses.size() > 0) {
                    String s= addresses.get(0).getCountryName()+","+ addresses.get(0).getCountryCode()+
                            " , rue  "+addresses.get(0).getFeatureName() +
                            " , code postale  " + addresses.get(0).getPostalCode()+
                            ","+ addresses.get(0).getSubLocality()
                            /*+"," +addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea()*/  ;

                    return s;
                }
            }
        }
return"";

    }


    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onResume() {

        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.hp.location/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.hp.location/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


}




