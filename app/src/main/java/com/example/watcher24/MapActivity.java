package com.example.watcher24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watcher24.api.GoogleMapsApi;
import com.example.watcher24.api.NearbyPlacesApiResponse;
import com.example.watcher24.helper.MapCustomizer;
import com.example.watcher24.model.Area;
import com.example.watcher24.model.PlaceName;
import com.example.watcher24.places.NearByPlace;

import com.example.watcher24.util.Constants;
import com.example.watcher24.util.MapUtils;
import com.example.watcher24.util.PreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    public static final String TAG="MapActivity";
    private Marker marker=null;
    private ImageView imageView1,imageView2;
    private TextView textView;
    private LatLng lastClickedLocation=null;
    private MapCustomizer mapCustomizer;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        imageView1=findViewById(R.id.add_to_playlist);
        imageView2=findViewById(R.id.save_location);
        textView=findViewById(R.id.textView_city_state_country);
        db=FirebaseFirestore.getInstance();
        preferenceManager=new PreferenceManager(getApplicationContext());
        initMap();
        setListeners();
    }

    private void setListeners() {
        imageView2.setOnClickListener(v->{
            Toast.makeText(this, "Showing nearby shelters...", Toast.LENGTH_SHORT).show();
            if(lastClickedLocation!=null)
            //showNearByShelters(lastClickedLocation);
                showNearByShelters(new LatLng(22.289199,91.144351));
            else{
                Toast.makeText(getApplicationContext(), "You have to choose a location", Toast.LENGTH_SHORT).show();
            }
        });

        imageView1.setOnClickListener(v->{
            saveLocation();
        });
    }

    private void saveLocation() {
        if(lastClickedLocation!=null){
            PlaceName placeName=getCityStateCountry(lastClickedLocation);
            String area_name="";
            if(placeName.getCity()!=null)
                area_name=placeName.getCity()+","+placeName.getState()+","+placeName.getCountry();
            else{
                area_name=placeName.getState()+","+placeName.getCountry();
            }
            textView.setText(area_name);
            saveInDatabase(area_name);
        }else{
            Log.d(TAG, "saveLocation: "+"You have to choose location and save it");
        }

    }

    private void saveInDatabase(String area_name) {
        Area area=new Area(area_name,"46874","10%","$4.54 billion","4 month 5 day 6 hours 5 min 6 sec");
        area.setUserId(preferenceManager.getString(Constants.KEY_USER_ID));
        db.collection(Constants.KEY_AREA_COLLECTION).add(area).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
               area.setDocumentId(documentReference.getId());
                Log.d(TAG, "onSuccess: "+area.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }


    private void showNearByShelters(LatLng latLng){
        map.clear();
        makePlacesCall("school", latLng);
    }


    private void makePlacesCall(String type, LatLng latLng) {
        String location = latLng.latitude + "," + latLng.longitude;
        Call<NearbyPlacesApiResponse> call = GoogleMapsApi.instance.placesService
                .fetchNearByPlaces(location, 15000, type, BuildConfig.GOOGLE_MAP_WEB_API_KEY);

        call.enqueue(new Callback<NearbyPlacesApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlacesApiResponse> call, @NonNull Response<NearbyPlacesApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body());
                    showNearByPlaces(response.body().getResults(),latLng);
                } else {
                    Log.e(TAG, "onResponse: " + type + " fetching is not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlacesApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: error fetching " + type, t);
            }
        });
    }

    public void showNearByPlaces(List<NearByPlace> nearByPlaceList,LatLng latLng_para) {
        if (nearByPlaceList.size() == 0){
            Toast.makeText(this, "No nearby locations", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(getApplicationContext(), "Total places "+nearByPlaceList.size(), Toast.LENGTH_SHORT).show();

        List<LatLng> latLngList = new ArrayList<>();


            NearByPlace minNearByPlace = nearByPlaceList.get(0);
            Marker minMarker = mapCustomizer.addMarker(latLng_para, minNearByPlace.getName());
            double minDist = MapUtils.distance(latLng_para, minNearByPlace.getGeometry().getLocation().getLatLng());
            for (int i = 1; i <nearByPlaceList.size() ; i++) {
                NearByPlace nearByPlace = nearByPlaceList.get(i);
                LatLng latLng = nearByPlace.getGeometry().getLocation().getLatLng();
                Marker marker = mapCustomizer.addMarker(latLng, nearByPlace.getName());

                double dist = MapUtils.distance(latLng_para, latLng);
                if( dist<minDist){
                    minDist = dist;
                    minNearByPlace = nearByPlace;
                    minMarker = marker;
                }

                latLngList.add(latLng);
            }
            minMarker.remove();
            minMarker = mapCustomizer.addMarker(minNearByPlace.getGeometry().getLocation().getLatLng(), "Nearest: "+minNearByPlace.getName());
            //https://stackoverflow.com/questions/14579426/google-android-maps-api-v2-show-marker-title-always
            // minMarker.showInfoWindow();
            minMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));




        mapCustomizer.animateCameraWithBounds(latLngList);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private PlaceName getCityStateCountry(LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String cityBd=state.split(" ")[0].toLowerCase();
            Log.d(TAG, "getCityStateCountry: "+cityBd);
            callApi();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            PlaceName placeName=new PlaceName(city,state,country);
            Log.d(TAG, "onMapClick: "+city+","+state+","+country);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return placeName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PlaceName();
    }

    private void callApi() {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mapCustomizer = new MapCustomizer(map);
        // ...
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.d(TAG, "onMapClick: "+latLng.toString());
                lastClickedLocation=latLng;

                if(marker!=null){
                    marker.remove();
                }

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    Log.d(TAG, "onMapClick: "+city+","+state+","+country);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }


                marker=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("My location"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
               // map.getUiSettings().setMyLocationButtonEnabled(true);
            }
        });
        // Turn on the My Location layer and the related control on the map.
        //updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}