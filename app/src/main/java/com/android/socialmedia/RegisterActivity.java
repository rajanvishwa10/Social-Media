package com.android.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering");
        progressDialog.setMessage("Uploading data...");
    }

    public void register(View view) {
        EditText editText = findViewById(R.id.fullname);
        EditText editText2 = findViewById(R.id.email);
        EditText editText3 = findViewById(R.id.username);
        EditText editText4 = findViewById(R.id.password);
        EditText editText5 = findViewById(R.id.cpassword);

        String name = editText.getText().toString().trim();
        String email = editText2.getText().toString().trim();
        String username = editText3.getText().toString().trim();
        String password = editText4.getText().toString().trim();
        String cpassword = editText5.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (name.isEmpty()) {
            editText.setError("Enter Name");
            editText.requestFocus();
        } else if (email.isEmpty()) {
            editText2.setError("Enter Email");
            editText2.requestFocus();
        }
        else if (!email.matches(emailPattern))
        {
            editText2.setError("Enter proper Email");
            editText2.requestFocus();
        }
        else if (username.isEmpty()) {
            editText3.setError("Enter Username");
            editText3.requestFocus();
        } else if (password.isEmpty() || password.length() < 6) {
            editText4.setError("Password must be greater than 5");
            editText4.requestFocus();
        } else if (!password.equals(cpassword)) {
            editText4.setError("Password should match");
            editText4.requestFocus();
        } else {
            progressDialog.show();
            addData(name, email, username, password);
        }
    }

    private void addData(String name, String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("FullName", name);
                            hashMap.put("Username", username);
                            hashMap.put("Email", email);
                            hashMap.put("post", "0");
                            hashMap.put("followers", "0");
                            hashMap.put("following", "0");
                            hashMap.put("profileImage", "");
                            FirebaseDatabase.getInstance().getReference("Users").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.cancel();
                                        Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.cancel();
                                    }
                                }
                            });
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


}