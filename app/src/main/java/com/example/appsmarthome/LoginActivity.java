package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appsmarthome.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private TextInputLayout email, password;
    private Button btnLogin;
    private TextView btnSignup, btnForgot;
    private ProgressBar progressBar;
    private CheckBox cbRemember;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Boolean saveLogin;
    private String _email = "", _password = "";
    FirebaseAuth auth;

    public void login() {
        if (TextUtils.isEmpty(_email)) {
            email.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(_password)) {
            password.setError("Password is required");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        email = binding.etEmail;
        password = binding.etPassword;
        btnLogin = binding.btnLogin;
        btnSignup = binding.tvSignup;
        btnForgot = binding.tvForgot;
        progressBar = binding.progressBar;
        cbRemember = binding.cbRemember;
        preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
        editor = preferences.edit();
        saveLogin = preferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            email.getEditText().setText(preferences.getString("email", ""));
            password.getEditText().setText(preferences.getString("password", ""));
            cbRemember.setChecked(true);
        }
        auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _email = email.getEditText().getText().toString().trim();
                _password = password.getEditText().getText().toString().trim();
                if (cbRemember.isChecked()) {
                    editor.putBoolean("saveLogin", true);
                    editor.putString("email", _email);
                    editor.putString("password", _password);
                } else {
                    editor.clear();
                }
                editor.commit();
                login();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
                passwordReset.setTitle("Reset Password");
                passwordReset.setMessage("Enter your email for reset password");
                passwordReset.setView(resetMail);
                passwordReset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String _email = resetMail.getText().toString().trim();
                        auth.sendPasswordResetEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Link send to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordReset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                passwordReset.create().show();
            }
        });
    }
}