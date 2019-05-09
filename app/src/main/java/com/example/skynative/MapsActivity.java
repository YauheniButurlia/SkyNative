package com.example.skynative;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private String TAG = MapsActivity.class.getSimpleName();
  private List<LatLng> list = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   *
   *
   * https://data.sfgov.org/resource/se33-6ad4.json?$limit=120
   *
   *
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    // Add a marker in Sydney and move the camera
    LatLng sF = new LatLng(37.78825, -122.4324);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sF,10));


    new GetPoints(mMap).execute();
  }


  private class GetPoints extends AsyncTask<Void, Void, Void> {

    private GoogleMap map;

    GetPoints(GoogleMap map) {
      this.map = map;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      Toast.makeText(MapsActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
      HttpHandler sh = new HttpHandler();
      // Making a request to url and getting response
      String url = "https://data.sfgov.org/resource/se33-6ad4.json?$limit=500";
      String jsonStr = sh.makeServiceCall(url);

      Log.e(TAG, "Response from url: " + jsonStr);
      if (jsonStr != null) {
        try {
          //JSONObject jsonObj = new JSONObject(jsonStr);
          JSONArray jsonArray = new JSONArray(jsonStr);

          for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject c = jsonArray.getJSONObject(i);
            Double latitude = c.getDouble("latitude");
            Double longitude = c.getDouble("longitude");

            LatLng latLng = new LatLng(latitude, longitude);

            // adding contact to contact list
            list.add(latLng);
          }
        } catch (final JSONException e) {
          Log.e(TAG, "Json parsing error: " + e.getMessage());
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(getApplicationContext(),
                      "Json parsing error: " + e.getMessage(),
                      Toast.LENGTH_LONG).show();
            }
          });

        }

      } else {
        Log.e(TAG, "Couldn't get json from server.");
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getApplicationContext(),
                    "Couldn't get json from server. Check LogCat for possible errors!",
                    Toast.LENGTH_LONG).show();
          }
        });
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
      for(LatLng l : list) {
        map.addMarker(new MarkerOptions().position(l));
      }
      /*
      ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
              R.layout.list_item, new String[]{ "email","mobile"},
              new int[]{R.id.email, R.id.mobile});
      lv.setAdapter(adapter);
      */
    }
  }
}
