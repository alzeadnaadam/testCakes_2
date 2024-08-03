package com.example.testcakes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private ArrayList<Product> products;
    private OnAddToCartClickListener listener;
    private User user;

    public ProductAdapter(Context context, ArrayList<Product> products, OnAddToCartClickListener listener, User user) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
        this.listener = listener;
        this.user = user; // Initialize the user
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }

        // Lookup view for data population
        TextView productNameTextView = convertView.findViewById(R.id.productNameTextView);
        TextView productPriceTextView = convertView.findViewById(R.id.productPriceTextView);
        TextView quantityTextView = convertView.findViewById(R.id.quantityTextView);
        Button increaseQuantityButton = convertView.findViewById(R.id.increaseQuantityButton);
        Button decreaseQuantityButton = convertView.findViewById(R.id.decreaseQuantityButton);
        Button addToCartButton = convertView.findViewById(R.id.addToCartButton);

        // Populate the data into the template view using the data object
        if (product != null) {
            productNameTextView.setText(product.getProductName());
            productPriceTextView.setText("$" + product.getPrice());
        }

        // Initialize quantity
        int[] quantity = {1};

        // Set a click listener for increase quantity button
        increaseQuantityButton.setOnClickListener(v -> {
            quantity[0]++;
            quantityTextView.setText(String.valueOf(quantity[0]));
        });

        // Set a click listener for decrease quantity button
        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                quantityTextView.setText(String.valueOf(quantity[0]));
            }
        });

        // Set a click listener for the add to cart button
        addToCartButton.setOnClickListener(v -> {
            if (listener != null && product != null) {
                listener.onAddToCartClick(product);
                saveOrderToFirebase(product, quantity[0]); // Save order to Firebase with quantity
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    // Define an interface for handling add to cart button clicks
    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    // Method to save order to Firebase
    private void saveOrderToFirebase(Product product, int quantity) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");

        String userId = user.getPhone(); // Assuming the phone number is unique and used as the key
        DatabaseReference cartRef = usersRef.child(userId).child("MyCart");

        String cartItemId = cartRef.push().getKey();
        if (cartItemId != null) {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Cart cart = new Cart(product.getProductName(), date, product.getPrice(), user.getPhone(), quantity);
            cartRef.child(cartItemId).setValue(cart);
        }
    }
}
