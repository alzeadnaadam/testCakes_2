package com.example.testcakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecyclerView;
    private ListView productListView;
    private DatabaseReference mDatabase;
    private ArrayList<Category> categoryList;
    private ArrayList<Product> productList;
    private User user;

    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.recyclerView);
        productListView = view.findViewById(R.id.productListView);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        // Set up RecyclerView for categories to display horizontally
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);

        fetchCategories();

        return view;
    }

    private void fetchCategories() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                // Set adapter for RecyclerView
                CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList, category -> fetchProductsForCategory(category));
                categoryRecyclerView.setAdapter(categoryAdapter);
                // Automatically show products for the first category if available
                if (!categoryList.isEmpty()) {
                    fetchProductsForCategory(categoryList.get(0)); // Fetch products for the first category
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductsForCategory(Category category) {
        DatabaseReference productRef = mDatabase.child(category.getName()).child("products");
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }

                // Use the custom ProductAdapter
                if (!productList.isEmpty() && getContext() != null) {
                    ProductAdapter.OnAddToCartClickListener listener = product -> {
                        // Handle additional actions on add to cart click if needed
                        Toast.makeText(getContext(), product.getProductName() + " added to cart!", Toast.LENGTH_SHORT).show();
                    };

                    ProductAdapter productAdapter = new ProductAdapter(getContext(), productList, listener, user);
                    productListView.setAdapter(productAdapter);
                } else {
                    productListView.setAdapter(null); // Clear the adapter if no products are available
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
