package com.app.journeyfinder;
//Important imports
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

public class LoginActivity extends AppCompatActivity {

    //setting the variables
    private EditText login_email, login_pass;
    private Button loginButton, signupButton, forgotPass;
    //Used for firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Checking if the user has been logged in
        // FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // if (currentUser != null) {
        //     startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        //     finish();
        // }
        setContentView(R.layout.activity_login);

        //Initialise firebase
        mAuth = FirebaseAuth.getInstance();
        //variables linked to XML IDs
        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        forgotPass = findViewById(R.id.forgotPass);
        //Login
        loginButton.setOnClickListener(v -> loginUser());
        //Sign-Up
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        //Forgot password
        forgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Bypass authentication for development/testing
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        
        /* Original login logic commented out to avoid billing/API issues
        String email = login_email.getText().toString();
        String password = login_pass.getText().toString();

        //Input validation
        if (TextUtils.isEmpty(email)) {
            login_email.setError("Enter your email");
            login_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login_email.setError("Enter a valid email");
            login_email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            login_pass.setError("Enter your password");
            login_pass.requestFocus();
            return;
        }

        //From Firebase login Documentation
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LOGIN", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        */
    }
}
