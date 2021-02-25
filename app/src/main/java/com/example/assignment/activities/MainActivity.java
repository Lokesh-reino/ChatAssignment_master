package com.example.assignment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.assignment.R;
import com.example.assignment.adapters.MyFragmentAdapter;
import com.example.assignment.helpers.AndroidContactsChangeListener;
import com.example.assignment.viewmodel.CreateEntryViewModel;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    Toolbar toolbar;
    ViewPager viewPager;
    MyFragmentAdapter adapter;

    private final int READ_CONTACT_REQUEST_CODE = 100;
    CreateEntryViewModel fragmentViewModel;

    AndroidContactsChangeListener.IChangeListener contactChangeListener = new AndroidContactsChangeListener.IChangeListener() {
        @Override
        public void onContactsChanged() {
            fragmentViewModel.completeContactSync();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("User Info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        adapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void checkPermissionSyncContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
        else
            syncContacts();
    }

    private void syncContacts() {
        fragmentViewModel.completeContactSync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACT_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                syncContacts();
            else
                Toast.makeText(this, "Contact Sync failed. Please grant contacts permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        setMenu(menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                CreateEntryViewModel.setQueryString(query);
                Log.d("TAG", "Inside onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                CreateEntryViewModel.setQueryString(newText);
                Log.d("TAG", "Inside onQueryTextChange: " + newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    private void setMenu(Menu menu) {
        CreateEntryViewModel.getIsMultiSelectOn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    menu.findItem(R.id.action_delete).setVisible(true);
                    menu.findItem(R.id.app_bar_search).setVisible(false);
                } else {
                    menu.findItem(R.id.action_delete).setVisible(false);
                    menu.findItem(R.id.app_bar_search).setVisible(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidContactsChangeListener.getInstance(this).stopContactsObservation();
    }

}