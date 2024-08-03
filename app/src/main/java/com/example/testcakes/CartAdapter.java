package com.example.testcakes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<Cart> cartList;
    private OnCartItemDeleteListener deleteListener;

    public interface OnCartItemDeleteListener {
        void onCartItemDelete(int position);
    }

    public CartAdapter(ArrayList<Cart> cartList, OnCartItemDeleteListener deleteListener) {
        this.cartList = cartList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.productNameTextView.setText(cart.getProductName());
        holder.productPriceTextView.setText("₪" + cart.getCost());
        holder.productQuantityTextView.setText("Quantity: " + cart.getQuantity());

        // Set the delete button click listener
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onCartItemDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartList.size()) {
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
        }
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
