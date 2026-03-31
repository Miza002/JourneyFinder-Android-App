package com.app.journeyfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private BottomNavigationView bottomNav;
    private RecyclerView postRecycler;
    private PostAdapter postAdapter;
    private List<PostItem> postList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);

        //Profile
        profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CommActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //profile pic
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.defaultpfp)
                    .circleCrop()
                    .into(profileIcon);
        }

        //RecyclerView
        postRecycler = findViewById(R.id.postRecycler);
        postRecycler.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        postRecycler.setAdapter(postAdapter);

        firestore = FirebaseFirestore.getInstance();

        //to make new post button
        FloatingActionButton NewPost = findViewById(R.id.NewPost);
        NewPost.setOnClickListener(v -> {
            startActivity(new Intent(CommActivity.this, NewPostActivity.class));
        });
        //calls load post
        loadPosts();

        //Bottom Nav
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_nav) {
                startActivity(new Intent(CommActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.map_nav) {
                startActivity(new Intent(CommActivity.this, MapActivity.class));
                finish();
                return true;
            } else if (id == R.id.comm_nav) {
                return true;
            }
            return false;
        });
    }

    //loads from firestore
    private void loadPosts() {
        firestore.collection("community-posts").orderBy("timePosted",
                        com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    postList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String username = doc.getString("username");
                        String userProfileUrl = doc.getString("userProfileUrl");
                        String caption = doc.getString("caption");
                        String imageUrl = doc.getString("imageUrl");
                        long time = doc.getLong("timePosted") != null ? doc.getLong("timePosted") : 0;

                        postList.add(new PostItem(username, userProfileUrl, formatTime(time), caption, imageUrl
                        ));
                    }
                    postAdapter.notifyDataSetChanged();
                });
    }
    //set for how long ago posted
    private String formatTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        long minutes = diff / (60 * 1000);
        long hours = minutes / 60;
        long days = hours / 24;

        if (days >= 1) return days + " DAY" + (days > 1 ? "S" : "") + " AGO";
        if (hours >= 1) return hours + " HOUR" + (hours > 1 ? "S" : "") + " AGO";
        if (minutes >= 1) return minutes + " MINUTE" + (minutes > 1 ? "S" : "") + " AGO";
        return "JUST NOW";
    }
}
