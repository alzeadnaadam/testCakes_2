package com.example.testcakes;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ProfileFragment extends Fragment {

    private TextView userFirstNameTextView;
    private TextView userLastNameTextView;
    private TextView userEmailTextView;
    private TextView userPhoneTextView;
    private TextView myOrdersButton;
    private TextView logoutButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<String> ordersList;
    private ArrayAdapter<String> adapter;
    private User user;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        myOrdersButton = view.findViewById(R.id.myOrdersButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        userFirstNameTextView = view.findViewById(R.id.userFirstNameTextView);
        userLastNameTextView = view.findViewById(R.id.userLastNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        userPhoneTextView = view.findViewById(R.id.userPhoneTextView);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            userFirstNameTextView.setText(user.getFirstName());
            userLastNameTextView.setText(user.getLastName());
            userEmailTextView.setText(user.getEmail());
            userPhoneTextView.setText(user.getPhone());
        } else {
             userFirstNameTextView.setText("N/A");
            userLastNameTextView.setText("N/A");
            userEmailTextView.setText("N/A");
            userPhoneTextView.setText("N/A");
        }
        myOrdersButton.setOnClickListener(v -> showOrdersDialog());
        logoutButton.setOnClickListener(v -> logoutAndGoToLogin());

        return view;
    }

    private void showOrdersDialog() {
        ordersList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ordersList);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("My Orders");

        View customLayout = getLayoutInflater().inflate(R.layout.dialog_orders, null);
        builder.setView(customLayout);

        ListView ordersListView = customLayout.findViewById(R.id.ordersListView);
        ordersListView.setAdapter(adapter);

        builder.setPositiveButton("Close", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        fetchOrders();
    }

    private void fetchOrders() {
        if (user != null) {
            String userPhone = user.getPhone();
            mDatabase.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ordersList.clear();
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        String phoneNumber = orderSnapshot.child("phoneNumber").getValue(String.class);
                        if (phoneNumber != null && phoneNumber.equals(userPhone)) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                String formattedDate = formatDate(order.getDate());
                                ordersList.add("Date: " + formattedDate + "\nCost: ₪" + order.getCost());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors.
                }
            });
        }
    }

    private String formatDate(String timestamp) {
        try {
            // Define the input date format according to the expected format of the timestamp string
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse the timestamp string into a Date object
            Date date = inputFormat.parse(timestamp);

            // Define the output date format
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            // Format the Date object into the desired output string
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private void logoutAndGoToLogin() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}