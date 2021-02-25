package com.example.assignment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.assignment.R;
import com.example.assignment.models.User;
import com.example.assignment.viewmodel.CreateEntryViewModel;

public class DetailOfUserDisplay extends AppCompatActivity {
    ImageView imageViewDetail;
    TextView textViewName,textViewPhone,textViewBirthday;
    String user;
    String id;
    CreateEntryViewModel viewModel;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_user_display);
        init();
    }

    private void init() {

        imageViewDetail = findViewById(R.id.image_view_detail);
//        textViewName = findViewById(R.id.textViewPersonName);
//        textViewPhone = findViewById(R.id.textViewPhone);
//        textViewBirthday = findViewById(R.id.textViewDOB);

        toolbar = findViewById(R.id.detail_tool_bar);
        toolbar.setTitle("Show User Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewModel = ViewModelProviders.of(this).get(CreateEntryViewModel.class);
        user = getIntent().getStringExtra("User");

        if (user!= null) {
            imageViewDetail.setImageURI(Uri.parse(user));
        }
        else
        {
            Uri imgUri = Uri.parse("android.resource://com.example.assignment/" + R.drawable.ic_baseline_person_24);
            imageViewDetail.setImageURI(null);
            imageViewDetail.setImageURI(imgUri);
        }
//
//        viewModel.fetchDetailsFromDatabase(Integer.parseInt(id));
//        viewModel.getUser().observe(this, user -> {
//            textViewName.setText(user.getName());
//            textViewPhone.setText(user.getPhoneNumber());
//            textViewBirthday.setText(user.getBirthday());
//            if (user.getImage() != null) {
//                imageViewDetail.setImageURI(Uri.parse(user.getImage()));
//            }
//            else
//                {
//                Uri imgUri = Uri.parse("android.resource://com.example.assignment/" + R.drawable.ic_baseline_person_24);
//                imageViewDetail.setImageURI(null);
//                imageViewDetail.setImageURI(imgUri);
//            }
//        });
////        onBackPressed();
    }
}