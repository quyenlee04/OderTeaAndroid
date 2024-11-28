package com.example.oderteaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private EditText nameInput, phoneInput, addressInput;
    private RecyclerView orderItemsRecyclerView;
    private TextView totalPriceText;
    private Button confirmButton;
    private DatabaseHelper databaseHelper;
    private List<CartItem> cartItems;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initializeViews();
        loadCartItems();
        setupListeners();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        confirmButton = findViewById(R.id.confirmButton);
        databaseHelper = new DatabaseHelper(this);
    }

    private void loadCartItems() {
        cartItems = databaseHelper.getCartItems(this);
        OrderItemsAdapter adapter = new OrderItemsAdapter(cartItems);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderItemsRecyclerView.setAdapter(adapter);

        calculateTotal();
    }

    private void calculateTotal() {
        totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.format("%.2f đ", totalPrice));
    }

    private void setupListeners() {
        confirmButton.setOnClickListener(v -> processOrder());
    }

    private void processOrder() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String address = addressInput.getText().toString();


        String phonePattern = "^0\\d{9}$";
        if (!phone.matches(phonePattern)) {
            Toast.makeText(this, "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper.updateProductQuantities(cartItems);

        // Generate order and show invoice
        showInvoice(name, phone, address);

    }

    private void showInvoice(String name, String phone, String address) {
        Intent intent = new Intent(this, InvoiceActivity.class);
        intent.putExtra("customerName", name);
        intent.putExtra("customerPhone", phone);
        intent.putExtra("customerAddress", address);
        intent.putExtra("totalAmount", totalPrice);
        intent.putExtra("cartItems", new ArrayList<>(cartItems));
        startActivity(intent);

        // Clear cart after successful order
        databaseHelper.clearCart();
        finish();
    }
}
