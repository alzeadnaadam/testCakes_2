package com.example.testcakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private TextView totalPriceTextView;
    private MaterialButton checkoutButton;
    private DatabaseReference mDatabase;
    private ArrayList<Cart> cartList;
    private User user;
    private double total;
    private CartAdapter cartAdapter;

    public static CartFragment newInstance(User user) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        if (user == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return view;
        }

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getPhone()).child("MyCart");
        fetchCartItems();

        checkoutButton.setOnClickListener(v -> handleCheckout());

        return view;
    }

    private void fetchCartItems() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartList.clear();
                total = 0;
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Cart cart = snapshot.getValue(Cart.class);
                        if (cart != null) {
                            cart.setId(snapshot.getKey()); // Set the Firebase key as the id
                            cartList.add(cart);
                            total += cart.getCost() * cart.getQuantity();
                        }
                    }
                }
                totalPriceTextView.setText("Total: ₪" + total);
                cartAdapter = new CartAdapter(cartList, position -> deleteCartItem(position));
                cartRecyclerView.setAdapter(cartAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCartItem(int position) {
        if (position >= 0 && position < cartList.size()) {
            Cart cart = cartList.get(position);
            String cartItemKey = cart.getId(); // Get the Firebase key
            mDatabase.child(cartItemKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    cartList.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    total -= cart.getCost() * cart.getQuantity();
                    totalPriceTextView.setText("Total: ₪" + total);
                    Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to remove item from cart", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        String orderId = ordersRef.push().getKey();
        if (orderId != null) {
            Order order = new Order(total, System.currentTimeMillis() + "", user.getPhone());

            ordersRef.child(orderId).setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    clearCart();
                } else {
                    Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Failed to generate order ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearCart() {
        mDatabase.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartList.clear();
                cartAdapter.notifyDataSetChanged();
                totalPriceTextView.setText("Total: ₪0");
            } else {
                Toast.makeText(getContext(), "Failed to clear cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
