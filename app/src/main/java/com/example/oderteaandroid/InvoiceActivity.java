package com.example.oderteaandroid;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InvoiceActivity extends AppCompatActivity {
    private TextView customerNameText, customerPhoneText, customerAddressText, totalAmountText;
    private RecyclerView invoiceItemsRecyclerView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        initializeViews();
        displayInvoiceDetails();
    }

    private void initializeViews() {
        customerNameText = findViewById(R.id.customerNameText);
        customerPhoneText = findViewById(R.id.customerPhoneText);
        customerAddressText = findViewById(R.id.customerAddressText);
        totalAmountText = findViewById(R.id.totalAmountText);
        invoiceItemsRecyclerView = findViewById(R.id.invoiceItemsRecyclerView);
        databaseHelper = new DatabaseHelper(this);
    }

    private void displayInvoiceDetails() {
        String name = getIntent().getStringExtra("customerName");
        String phone = getIntent().getStringExtra("customerPhone");
        String address = getIntent().getStringExtra("customerAddress");
        double totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        ArrayList<CartItem> cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");


        customerNameText.setText(name);
        customerPhoneText.setText(phone);
        customerAddressText.setText(address);
        totalAmountText.setText(String.format("Tổng tiền: %.2f đ", totalAmount));

        // Display order items
        OrderItemsAdapter adapter = new OrderItemsAdapter(cartItems);
        invoiceItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invoiceItemsRecyclerView.setAdapter(adapter);
    }
}
