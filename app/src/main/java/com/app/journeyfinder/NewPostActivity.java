package com.app.journeyfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private ImageView postImagePreview;
    private Button selectImageButton, uploadPostButton;
    private EditText captionInput;
    private Uri selectedImageUri;
    private FirebaseUser currentUser;
    private StorageReference storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        postImagePreview = findViewById(R.id.postImagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        uploadPostButton = findViewById(R.id.uploadPostButton);
        captionInput = findViewById(R.id.captionInput);

        //Firebase connection
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference("communityPosts");
        firestore = FirebaseFirestore.getInstance();

        //Image
        ActivityResultLauncher<Intent> imagePick = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(this).load(selectedImageUri).into(postImagePreview);
                    }
                });

        //Select Image
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePick.launch(intent);
        });

        //Upload Post
        uploadPostButton.setOnClickListener(v -> {
            String caption = captionInput.getText().toString();

            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }
            if (caption.isEmpty()) {
                Toast.makeText(this, "Caption cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(NewPostActivity.this, CommActivity.class));
            finish();
            uploadImage(caption);
        });
    }

    private void uploadImage(String caption) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storage.child(fileName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            savePost(downloadUri.toString(), caption);
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    //saves to firestore
    private void savePost(String imageUrl, String caption) {
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "no name";
        String profileUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";

        //key paire for database
        Map<String, Object> post = new HashMap<>();
        post.put("username", userName);
        post.put("userProfileUrl", profileUrl);
        post.put("caption", caption);
        post.put("imageUrl", imageUrl);
        post.put("timePosted", System.currentTimeMillis());

        firestore.collection("community-posts").add(post).addOnSuccessListener(
                documentReference -> {
            Toast.makeText(this, "Post uploaded", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to save post: " + e.getMessage(),
                Toast.LENGTH_SHORT).show());
    }
}
