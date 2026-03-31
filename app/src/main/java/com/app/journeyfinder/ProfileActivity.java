package com.app.journeyfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 2002;

    private TextView usernameText;
    private ImageView profileImage;
    private Button logoutButton, redeemButton, premiumButton;
    private BottomNavigationView bottomNav;
    private FirebaseUser mAuth;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("profilePics");

        //variables xml
        usernameText = findViewById(R.id.usernameText);
        profileImage = findViewById(R.id.profileImage);
        logoutButton = findViewById(R.id.logoutButton);
        redeemButton = findViewById(R.id.redeemButton);
        premiumButton = findViewById(R.id.joinPremiumButton);

        //user info
        if (mAuth != null) {
            String name = mAuth.getDisplayName();
            Uri photo = mAuth.getPhotoUrl();
            if (name != null) {
                usernameText.setText(name.toUpperCase());
            }
            if (photo != null) {
                Glide.with(this).load(photo).placeholder(R.drawable.defaultpfp)
                        .into(profileImage);
            }
        }

        //upload profile pic
        profileImage.setOnClickListener(v -> openImage());

        //Logout
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        //redeem and premium
        redeemButton.setOnClickListener(v ->
                Toast.makeText(this, "Redeem Feature Coming Soon", Toast.LENGTH_SHORT).show());

        premiumButton.setOnClickListener(v ->
                Toast.makeText(this, "Premium Feature Coming Soon", Toast.LENGTH_SHORT).show());

        //Bottom vav
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_nav) { startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.map_nav) {
                startActivity(new Intent(ProfileActivity.this, MapActivity.class));
                return true;
            } else if (id == R.id.comm_nav) {
                startActivity(new Intent(ProfileActivity.this, CommActivity.class));
                return true;
            }
            return false;
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select profile picture"), PICK_IMAGE_REQUEST);
    }
    //image to firebase storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri img = data.getData();
            uploadProImage(img);
        }
    }

    private void uploadProImage(Uri imageUri) {
        if (mAuth == null) return;
        StorageReference userRef = storageRef.child(mAuth.getUid() + ".jpg");
        userRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> userRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            //Save the download URL to the firebase profile
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri)
                                    .build();
                            mAuth.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Glide.with(this).load(downloadUri).placeholder(R.drawable.defaultpfp)
                                            .into(profileImage);

                                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })).addOnFailureListener(e ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
