package com.example.oderteaandroid;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        if (databaseHelper.getAllProducts().isEmpty()) {
            databaseHelper.insertSampleProducts();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        List<Product> products = databaseHelper.getAllProducts();
        adapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.about) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            } else if (item.getItemId() == R.id.user) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                return true;
            }
            return false;
        });

    }
    private void filterProducts(String text) {
        List<Product> filteredList = new ArrayList<>();
        List<Product> allProducts = databaseHelper.getAllProducts();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
            }
        }

        adapter.updateProducts(filteredList);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
}
