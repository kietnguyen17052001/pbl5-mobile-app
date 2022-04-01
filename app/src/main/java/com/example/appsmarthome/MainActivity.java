package com.example.appsmarthome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.appsmarthome.Objects.UserObj;
import com.example.appsmarthome.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {
    private static final String FULL_NAME = "fullName";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private ActivityMainBinding binding;
    private Button btnLogout, btnRemote, btnUser;
    private TextView username;
    private String userId;
    FirebaseAuth auth;
    FirebaseFirestore store;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        btnLogout = binding.btnLogout;
        btnRemote = binding.btnRemote;
        btnUser = binding.btnUser;
        username = binding.tvUsername;
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
//        username.setText(username.getText() + auth.getCurrentUser().getDisplayName());
        userId = auth.getCurrentUser().getUid();
        documentReference = store.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
//                    user.setFullName(documentSnapshot.getString(FULL_NAME));
//                    user.setEmail(documentSnapshot.getString(EMAIL));
//                    user.setPhone(documentSnapshot.getString(PHONE));
                    username.setText(username.getText() + documentSnapshot.getString(FULL_NAME));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RemoteActivity.class);
                startActivity(intent);
            }
        });
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (value.exists()){
//                    user.setFullName(value.getString(FULL_NAME));
//                    user.setEmail(value.getString(EMAIL));
//                    user.setPhone(value.getString(PHONE));
//                    username.setText(username.getText() + user.getFullName());
//                }
//            }
//        });
//    }
}