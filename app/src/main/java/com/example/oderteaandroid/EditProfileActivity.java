package com.example.oderteaandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView editAvatar;
    private EditText editPhone, editAddress;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        loadCurrentData();
        setupListeners();
    }

    private void initializeViews() {
        editAvatar = findViewById(R.id.editAvatar);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        saveButton = findViewById(R.id.saveButton);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
    }

    private void loadCurrentData() {
        String email = sharedPreferences.getString("email", "");
        UserData userData = databaseHelper.getUserData(email);

        if (userData != null) {
            editPhone.setText(userData.getPhone());
            editAddress.setText(userData.getAddress());
            if (userData.getAvatar() != null) {
                Glide.with(this).load(userData.getAvatar()).into(editAvatar);
            }
        }
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> {
            String email = sharedPreferences.getString("email", "");
            String phone = editPhone.getText().toString().trim();
            String address = editAddress.getText().toString().trim();

            if (databaseHelper.updateUserProfile(email, phone, address)) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
