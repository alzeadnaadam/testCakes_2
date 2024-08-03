package com.example.testcakes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    private TextView welcomeText;
    private BottomNavigationView nav;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.welcomeText);

        // Retrieve the user object from the intent
        user = getIntent().getParcelableExtra("user");

        if (user != null) {
            welcomeText.setText("Welcome, " + user.getFirstName());
        }

        boolean navigateToFragment = getIntent().getBooleanExtra("navigateToFragment", false);
        if (navigateToFragment) {
            // Replace the current fragment with the desired fragment (HomeFragment)
            replaceFragment(HomeFragment.newInstance(user));
        } else {
            // Load the default fragment (HomeFragment)
            replaceFragment(HomeFragment.newInstance(user));
        }

        nav = findViewById(R.id.bottom_navigation_view);
        nav.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance(user);
                    break;
                case R.id.navigation_shop:
                    selectedFragment =  CartFragment.newInstance(user);
                    break;
                case R.id.navigation_profile:
                    selectedFragment = ProfileFragment.newInstance(user);
                    break;
            }
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        }
    };

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}