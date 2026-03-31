package com.app.journeyfinder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {

    private Button resetButton;
    private Button backButton;
    private EditText resetEmailInput;
    private FirebaseAuth mAuth;
    private String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        //variables linked to XML IDs
        backButton = findViewById(R.id.Back);
        resetButton = findViewById(R.id.Reset);
        resetEmailInput = findViewById(R.id.Restart_email);

        mAuth = FirebaseAuth.getInstance();

        //Reset password
        resetButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               strEmail = resetEmailInput.getText().toString();
               if (!TextUtils.isEmpty(strEmail)) {
                   resetPassword();
               } else {
                   resetEmailInput.setError("Please enter your email");
               }
           }
        });
        //Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //Firebase reset passwords
    private void resetPassword() {
        mAuth.sendPasswordResetEmail(strEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassActivity.this,
                                    "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }
}
