package com.app.journeyfinder;
//imports
import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private EditText sign_username, sign_email, confirm_email, sign_pass, confirm_pass;
    private Button signInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Initialise firebase
        mAuth = FirebaseAuth.getInstance();
        //variables linked to XML IDs
        sign_username = findViewById(R.id.sign_username);
        sign_email = findViewById(R.id.sign_email);
        confirm_email = findViewById(R.id.confirm_email);
        sign_pass = findViewById(R.id.sign_pass);
        confirm_pass = findViewById(R.id.confirm_pass);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = sign_username.getText().toString();
        String email = sign_email.getText().toString();
        String confirmEmail = confirm_email.getText().toString();
        String password = sign_pass.getText().toString();
        String confirmPassword = confirm_pass.getText().toString();
        //setting username
        if (TextUtils.isEmpty(name)) {
            sign_username.setError("Enter your username");
            sign_username.requestFocus();
            return;
        }
        //checking valid email
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sign_email.setError("Enter a valid email");
            return;
        }
        //checks email confirmation
        else if (!email.equals(confirmEmail)) {
            confirm_email.setError("Emails do not match");
            return;
        }
        //sets password minimum length
        if (TextUtils.isEmpty(password)) {
            sign_pass.setError("Enter your password");
            sign_pass.requestFocus();
            return;
        }
        else if (password.length() < 6) {
            sign_pass.setError("Password has to be at least 6 characters");
            return;
        }
        //checks passwords confirmation
        else if (!password.equals(confirmPassword)) {
            confirm_pass.setError("Passwords do not match");
            return;
        }

        //Firebase sign in and update user profile
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Sets display name
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            if (profileTask.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        });
                            }
                            //takes to the home page
                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
