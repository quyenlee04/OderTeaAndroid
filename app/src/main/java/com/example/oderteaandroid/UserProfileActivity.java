package com.example.oderteaandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView userAvatar, btnBack;
    private TextView userEmail, userPhone, userAddress;
    private Button editProfileButton, logoutButton;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final int EDIT_PROFILE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeViews();
        loadUserData();
        setupListeners();
    }

    private void initializeViews() {
        userAvatar = findViewById(R.id.userAvatar);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userAddress = findViewById(R.id.userAddress);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
    }

    private void loadUserData() {
        String email = sharedPreferences.getString("email", "");
        UserData userData = databaseHelper.getUserData(email);

        if (userData != null) {
            userEmail.setText(email);
            userPhone.setText("Phone: " + (userData.getPhone() != null && !userData.getPhone().isEmpty()
                    ? userData.getPhone() : "Add phone number"));
            userAddress.setText("Address: " + (userData.getAddress() != null && !userData.getAddress().isEmpty()
                    ? userData.getAddress() : "Add address"));

            if (userData.getAvatar() != null && !userData.getAvatar().isEmpty()) {
                Glide.with(this)
                        .load(userData.getAvatar())
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(userAvatar);
            }
        }
    }



    private void setupListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        logoutButton.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            loadUserData();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

}
