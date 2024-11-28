package com.example.oderteaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private TextView totalPriceTextView;
    private MaterialButton checkoutButton;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseHelper databaseHelper;

    private List<CartItem> getCartItems() {
        return databaseHelper.getCartItems(this);
    }

    private void updateItemQuantity(int position, int newQuantity) {
        CartItem item = cartItems.get(position);
        if (newQuantity <= 0) {
            removeItem(position);
        } else {
            if (databaseHelper.updateCartItemQuantity(item.getId(), newQuantity)) {
                item.setQuantity(newQuantity);
                updateTotalPrice();
                cartAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databaseHelper = new DatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        setupListeners();
        updateTotalPrice();
    }

    private void initializeViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        cartItems = getCartItems(); // Get items from database
        cartAdapter = new CartAdapter(cartItems, this::updateItemQuantity, this::removeItem);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
        cartRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setupListeners() {
        checkoutButton.setOnClickListener(v -> processCheckout());
    }



    private void removeItem(int position) {
        CartItem item = cartItems.get(position);
        if (databaseHelper.removeFromCart(item.getId())) {
            cartItems.remove(position);
            cartAdapter.notifyItemRemoved(position);
            updateTotalPrice();
        }
    }


    private void processCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartItems.clear();
        cartItems.addAll(getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }
    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("%.2f đ", total));
    }

}
