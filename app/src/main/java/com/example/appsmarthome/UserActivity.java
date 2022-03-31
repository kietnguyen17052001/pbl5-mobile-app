package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.appsmarthome.Objects.UserObj;
import com.example.appsmarthome.databinding.ActivityUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends AppCompatActivity {
    private static final String FULL_NAME = "fullName";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private String userId;
    private ActivityUserBinding binding;
    private EditText name, email, phone;
    FirebaseAuth auth;
    FirebaseFirestore store;
    DocumentReference documentReference;
    UserObj user = new UserObj();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        name = binding.etName;
        email = binding.etEmail;
        phone = binding.etPhone;
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        name.setText(user.getFullName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
    }
}