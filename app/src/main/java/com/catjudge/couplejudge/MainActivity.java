package com.catjudge.couplejudge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.catjudge.couplejudge.fragment.HallFragment;
import com.catjudge.couplejudge.fragment.NotebookFragment;
import com.catjudge.couplejudge.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_OPEN_TAB = "open_tab";
    public static final String EXTRA_CLEAR_HALL_FORM = "clear_hall_form";
    private BottomNavigationView bottomNavigationView;
    private boolean shouldClearHallForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_hall) {
                switchFragment(HallFragment.newInstance(shouldClearHallForm));
                shouldClearHallForm = false;
                return true;
            } else if (item.getItemId() == R.id.nav_notebook) {
                switchFragment(new NotebookFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                switchFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(android.content.Intent intent) {
        shouldClearHallForm = intent.getBooleanExtra(EXTRA_CLEAR_HALL_FORM, shouldClearHallForm);
        String openTab = intent.getStringExtra(EXTRA_OPEN_TAB);
        if ("notebook".equals(openTab)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_notebook);
        } else if ("profile".equals(openTab)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_hall);
        }
    }

    private void switchFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
