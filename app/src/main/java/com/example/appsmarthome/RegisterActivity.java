package com.example.appsmarthome;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appsmarthome.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String FULL_NAME = "fullName";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private ActivityRegisterBinding binding;
    private EditText fullName, email, password, phone;
    private Button btnRegister;
    private TextView btnLogin;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseFirestore store;
    DocumentReference documentReference;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        fullName = binding.etName;
        email = binding.edEmail;
        password = binding.edPassword;
        phone = binding.edPhone;
        btnRegister = binding.btnSignup;
        btnLogin = binding.tvLogin;
        progressBar = binding.progressBar;
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _fullName = fullName.getText().toString().trim();
                String _phone = phone.getText().toString().trim();
                String _email = email.getText().toString().trim();
                String _password = password.getText().toString().trim();
                if (TextUtils.isEmpty(_email)){
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(_password)){
                    password.setError("Password is required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();
                            userId = auth.getCurrentUser().getUid();
                            documentReference = store.collection("users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put(FULL_NAME, _fullName);
                            user.put(EMAIL, _email);
                            user.put(PHONE, _phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "User is created for id:" + userId);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Failure:" + e.getMessage());
                                }
                            });
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}