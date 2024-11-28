package com.example.oderteaandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ImageButton facebookButton = findViewById(R.id.facebookButton);
        ImageButton instagramButton = findViewById(R.id.instagramButton);

        facebookButton.setOnClickListener(v -> {
            String facebookUrl = "https://www.facebook.com/quyenj09";
            openUrl(facebookUrl);
        });

        instagramButton.setOnClickListener(v -> {
            String instagramUrl = "https://www.instagram.com/quyenlei/";
            openUrl(instagramUrl);
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
