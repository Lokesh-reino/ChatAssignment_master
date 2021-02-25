package com.example.assignment;

import android.view.View;

import com.example.assignment.models.User;

public interface ItemClickListener {
    void onItemClicked(View view, User user);

    void onItemLongClicked(View view, User user, int index);


}
