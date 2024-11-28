package com.example.oderteaandroid;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.oderteaandroid.databinding.ActivityProductDetailBinding;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private DatabaseHelper databaseHelper;
    private Product product;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        int productId = getIntent().getIntExtra("product_id", -1);
        product = databaseHelper.getProductById(productId);

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        binding.productName.setText(product.getName());
        binding.productDescription.setText(product.getDescription());
        binding.productPrice.setText(String.format("$%.2f", product.getPrice()));
        binding.quantityText.setText(String.valueOf(quantity));

        binding.availableQuantity.setText("Còn lại: " + product.getQuantity());
        // Load image
        int resourceId = getResources().getIdentifier(
                product.getImage(),
                "drawable",
                getPackageName()
        );

        Glide.with(this)
                .load(resourceId)
                .centerCrop()
                .into(binding.productImage);
    }



        private void setupListeners() {
            binding.btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    binding.quantityText.setText(String.valueOf(quantity));
                }
            });

            binding.btnIncrease.setOnClickListener(v -> {
                if (quantity < product.getQuantity()) {
                    quantity++;
                    binding.quantityText.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(this, "Đã đạt số lượng tối đa", Toast.LENGTH_SHORT).show();
                }
            });

            binding.btnAddToCart.setOnClickListener(v -> {
                if (databaseHelper.addToCart(product.getId(), quantity)) {
                    Toast.makeText(this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Không đủ số lượng trong kho", Toast.LENGTH_SHORT).show();
                }
            });

            binding.btnBack.setOnClickListener(v -> finish());
    }

}
