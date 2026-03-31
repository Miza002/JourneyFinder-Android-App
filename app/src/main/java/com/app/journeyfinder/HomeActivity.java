package com.app.journeyfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private Button tabFood, tabShopping, tabEvents, tabLandmarks;
    private BottomNavigationView bottomNav;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Places API (Use your own API key)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR_PLACES_API_KEY_HERE");
        }
        placesClient = Places.createClient(this);

        //Permission for user
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2002
            );
        }
        //user location
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                loadNearbyPlaces(lat, lng);
            }
        });

        // Profile icon
        profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView profileIcon = findViewById(R.id.profileIcon);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.defaultpfp)
                    .circleCrop()
                    .into(profileIcon);
        }

        //Tabs
        tabFood = findViewById(R.id.tabFood);
        tabShopping = findViewById(R.id.tabShopping);
        tabEvents = findViewById(R.id.tabEvents);
        tabLandmarks = findViewById(R.id.tabLandmarks);

        tabFood.setOnClickListener(v -> tab("restaurant|cafe|bakery|food"));
        tabShopping.setOnClickListener(v -> tab("clothing_store|supermarket|furniture_store|" +
                "convenience_store|department_store|electronics_store"));
        tabEvents.setOnClickListener(v -> tab("stadium|place_of_worship|hindu_temple|church|" +
                "mosque|zoo|amusement_park|aquarium|night_club"));
        tabLandmarks.setOnClickListener(v -> tab("tourist_attraction|landmark|natural_feature|" +
                "art_gallery|park"));

        //Bottom nav
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_nav) {
                return true;
            } else if (id == R.id.map_nav) {
                startActivity(new Intent(HomeActivity.this, MapActivity.class));
                return true;
            } else if (id == R.id.comm_nav) {
                startActivity(new Intent(HomeActivity.this, CommActivity.class));
                return true;
            }
            return false;
        });
    }

    private void tab(String newTab) {
        Intent intent = new Intent(this, TabsActivity.class);
        intent.putExtra("tab", newTab);
        startActivity(intent);
    }

    //looks for nearby places information
    private void loadNearbyPlaces(double lat, double lng) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHOTO_METADATAS
        );
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(fields);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        placesClient.findCurrentPlace(request).addOnSuccessListener(response -> {
            List<PlaceItem> placeList = new ArrayList<>();
            //gets the info
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                Place place = placeLikelihood.getPlace();
                String name = place.getName();
                String address = place.getAddress();
                String photoUrl = null;
                PhotoMetadata metadata = null;
                if (place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()) {
                    metadata = place.getPhotoMetadatas().get(0);
                }
                Object type = null;
                //adds to place item
                placeList.add(new PlaceItem(name, address, photoUrl, metadata, type));
            }
            //shows it on recyclerveiw
            RecyclerView recyclerView = findViewById(R.id.recommendedRecycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new PlaceAdapter(this, placeList, placesClient));
        })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Places API failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
