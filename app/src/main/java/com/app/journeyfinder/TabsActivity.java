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
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabsActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private PlacesClient placesClient;
    private RecyclerView recyclerView;
    private String tabType;
    private Button tabFood, tabShopping, tabEvents, tabLandmarks;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        tabType = getIntent().getStringExtra("tab");
        if (tabType == null) {
            finish();
            return;
        }

        // Profile icon
        profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(TabsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView profileIcon = findViewById(R.id.profileIcon);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.journeyfinder_logo)
                    .circleCrop() // optional: make it circular
                    .into(profileIcon);
        }

        //Tabs
        tabFood = findViewById(R.id.tabFood);
        tabShopping = findViewById(R.id.tabShopping);
        tabEvents = findViewById(R.id.tabEvents);
        tabLandmarks = findViewById(R.id.tabLandmarks);

        tabFood.setOnClickListener(v -> tab("restaurant|cafe|bakery|food"));
        tabShopping.setOnClickListener(v -> tab("clothing_store|supermarket|furniture_store|convenience_store|department_store|electronics_store"));
        tabEvents.setOnClickListener(v -> tab("stadium|place_of_worship|mosque|hindu_temple|church|zoo|amusement_park|aquarium|night_club"));
        tabLandmarks.setOnClickListener(v -> tab("tourist_attraction|landmark|natural_feature|art_gallery|park"));

        //Bottom Nav
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_nav) { startActivity(new Intent(TabsActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.map_nav) {
                startActivity(new Intent(TabsActivity.this, MapActivity.class));
                return true;
            } else if (id == R.id.comm_nav) {
                startActivity(new Intent(TabsActivity.this, CommActivity.class));
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.categoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Places API (Use your own API key)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "YOUR_PLACES_API_KEY_HERE");
        }

        placesClient = Places.createClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2002
            );
        } else {
            getCurrentLocationAndLoad(tabType);
        }
    }

    //reopen the tab screen
    private void tab(String newTab) {
        Intent intent = new Intent(this, TabsActivity.class);
        intent.putExtra("tab", newTab);
        startActivity(intent);
    }

    //permisions fro user
    private void getCurrentLocationAndLoad(String tabType) {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                loadPlaces(lat, lng, tabType);
            }
        });
    }

    private void loadPlaces(double lat, double lng, String tabType) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.TYPES,
                Place.Field.PHOTO_METADATAS
        );

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(fields);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        placesClient.findCurrentPlace(request).addOnSuccessListener(response -> {
            String[] allowedTypes = tabType.split("\\|");
            List<PlaceItem> placeList = new ArrayList<>();
            for (PlaceLikelihood likelihood : response.getPlaceLikelihoods()) {
                Place place = likelihood.getPlace();
                if (place.getTypes() != null) {
                    for (Place.Type type : place.getTypes()) {
                        String typeStr = type.toString().toLowerCase();
                        for (String filter : allowedTypes) {
                            if (typeStr.equalsIgnoreCase(filter)) {
                                placeList.add(new PlaceItem(
                                        place.getName(),
                                        place.getAddress(),
                                        null,
                                        place.getPhotoMetadatas() != null && !place.getPhotoMetadatas().isEmpty()
                                                ? place.getPhotoMetadatas().get(0) : null,
                                        filter
                                ));
                            }
                        }
                    }
                }
            }
            recyclerView.setAdapter(new PlaceAdapter(this, placeList, placesClient));

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Places API failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
