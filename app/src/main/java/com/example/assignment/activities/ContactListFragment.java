package com.example.assignment.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.assignment.R;
import com.example.assignment.adapters.ContactListRecyclerviewAdapter;
import com.example.assignment.models.Contact;
import com.example.assignment.viewmodel.CreateEntryViewModel;


public class ContactListFragment extends Fragment {

    RecyclerView recyclerViewContactList;
    ContactListRecyclerviewAdapter recyclerviewAdapter;
    RecyclerView.LayoutManager layoutManager;
    CreateEntryViewModel fragmentViewModel;

    boolean isFragmentActive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentViewModel = new ViewModelProvider(this).get(CreateEntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        init(view);

        fragmentViewModel.contactInit();
        observeContactDB();

        observeQueryString();
        return view;
    }


    private void init(View view) {
        recyclerViewContactList = view.findViewById(R.id.contacts_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerviewAdapter = new ContactListRecyclerviewAdapter();

        recyclerViewContactList.setLayoutManager(layoutManager);
        recyclerViewContactList.setAdapter(recyclerviewAdapter);
    }

    private void observeContactDB() {
        fragmentViewModel.contactList.observe(getViewLifecycleOwner(), new Observer<PagedList<Contact>>() {
            @Override
            public void onChanged(PagedList<Contact> contacts) {
                recyclerviewAdapter.submitList(contacts);
            }
        });
    }

    private void observeQueryString() {
        fragmentViewModel.getQueryString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String query) {
                if (isFragmentActive)
                    queryContactList(query);
            }
        });
    }

    private void queryContactList(String query) {
        query = "%" + query + "%";

        fragmentViewModel.queryContactInit(query);

        fragmentViewModel.queryContactList.observe(this, new Observer<PagedList<Contact>>() {
            @Override
            public void onChanged(PagedList<Contact> contacts) {
                recyclerviewAdapter.submitList(contacts);
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        isFragmentActive = menuVisible;
    }


}