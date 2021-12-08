package com.idnp.laboratorio10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button button_location;
    TextView textView_location;
    LocationManager locationManager;

    Criteria criteria;
    Location location;
    String provider;
    double old_latitude; // latitude
    double old_longitude; // longitude
    double new_latitude; // latitude
    double new_longitude; // longitude
    String details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_location = findViewById(R.id.text_location);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(provider);

        try {
            old_latitude = location.getLatitude();
            old_longitude = location.getLongitude();
        } catch (Exception e) {

        }

        locationManager.requestLocationUpdates(provider, 10000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        location = locationManager.getLastKnownLocation(provider);
                        new_latitude = location.getLatitude();
                        new_longitude = location.getLongitude();

                        if (details == null) {
                            details = "Ubicación Actual:" + new_latitude + ", " + new_longitude;
                            details = details + "\n" + "Ubicación Antigua:" + old_latitude + ", " + old_longitude;
                        } else {

                            details = details + "\n" + "Ubicación Actual:" + new_latitude + ", " + new_longitude;
                            details = details + "\n" + "Ubicación Antigua:" + old_latitude + ", " + old_longitude;
                        }

                        Log.d("Proof","Archivo Iniciado");
                        try {
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("config.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write(details);
                            outputStreamWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String texto = readFromFile(getApplicationContext());
                        textView_location.setText(texto);

                        old_latitude = new_latitude;
                        old_longitude = new_longitude;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        System.out.println("**********This is StatusChanged**********" + 15000);
                        Toast.makeText(MainActivity.this, "StatusChanged" + Math.abs((float) old_latitude - (float) new_latitude) + "," + Math.abs((float) old_longitude - (float) new_longitude), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        System.out.println("**********This is ProviderEnabled**********" + 15000);
                        Toast.makeText(MainActivity.this, "ProviderDisabled" + Math.abs((float) old_latitude - (float) new_latitude) + "," + Math.abs((float) old_longitude - (float) new_longitude), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        System.out.println("**********this is ProviderDisabled**********" + 15000);
                        Toast.makeText(MainActivity.this, "ProviderDisabled" + Math.abs((float) old_latitude - (float) new_latitude) + "," + Math.abs((float) old_longitude - (float) new_longitude), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}