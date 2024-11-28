package com.example.oderteaandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private OnQuantityChangeListener quantityListener;
    private OnItemRemoveListener removeListener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged(int position, int newQuantity);
    }

    public interface OnItemRemoveListener {
        void onItemRemoved(int position);
    }

    public CartAdapter(List<CartItem> cartItems,
                       OnQuantityChangeListener quantityListener,
                       OnItemRemoveListener removeListener) {
        this.cartItems = cartItems;
        this.quantityListener = quantityListener;
        this.removeListener = removeListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantityText;
        ImageButton btnDecrease, btnIncrease, btnDelete;

        CartViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(CartItem item, final int position) {
            productName.setText(item.getName());
            productPrice.setText(String.format("%.2f đ", item.getPrice()));
            quantityText.setText(String.valueOf(item.getQuantity()));

            Glide.with(itemView.getContext())
                    .load(item.getImageResource())
                    .centerCrop()
                    .into(productImage);

            btnDecrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity <= 0) {
                    removeListener.onItemRemoved(position);
                } else {
                    quantityListener.onQuantityChanged(position, newQuantity);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                quantityListener.onQuantityChanged(position, item.getQuantity() + 1);
            });

            btnDelete.setOnClickListener(v -> {
                removeListener.onItemRemoved(position);
            });
        }
    }
}
