package com.example.assignment.activities;


import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.example.assignment.AlertDialogHelper;
import com.example.assignment.Constants;
import com.example.assignment.EditAndDeleteInterface;
import com.example.assignment.R;
import com.example.assignment.RecyclerItemClickListener;
import com.example.assignment.adapters.UserListAdapter;
import com.example.assignment.models.User;
import com.example.assignment.viewmodel.CreateEntryViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewUsersInRecyclerFragment extends Fragment implements EditAndDeleteInterface, AlertDialogHelper.AlertDialogListener {

    private CreateEntryViewModel createEntryViewModel;
    @BindView(R.id.user_recycler_view)
    RecyclerView UserList;
    boolean multiSelectStatus = false;
    List<User> currentUserList;
    ArrayList<User> deleteUserList = new ArrayList<>();
    Button txtEdit, txtDelete;
    SwipeRevealLayout swipeRevealLayout;
    ArrayList<User> userList;
    private UserListAdapter userListAdapter;
    ActionMode mActionMode;
    ArrayList<User> multiselect_list = new ArrayList<>();
    Menu context_menu;
    boolean isMultiSelect = false;
    AlertDialogHelper alertDialogHelper;

    public static ViewUsersInRecyclerFragment newInstance() {
        return new ViewUsersInRecyclerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show_users_in_recyclerview, container, false);
        userListAdapter = new UserListAdapter(this, getActivity());
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        currentUserList = new ArrayList<>();

        UserList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), UserList, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d("TAG", "Default intent called");
                if (isMultiSelect)
                    multi_select(position, view);
                else {
                    ViewEditActivityCalling(Constants.viewActionType, position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<>();
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        mActionMode = getActivity().startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position, view);
            }
        }));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createEntryViewModel = ViewModelProviders.of(getActivity()).get(CreateEntryViewModel.class);
        createEntryViewModel.fetchDataFromDatabase();

        UserList.setLayoutManager(new LinearLayoutManager(getContext()));
        txtDelete = view.findViewById(R.id.txtDelete);
        txtEdit = view.findViewById(R.id.txtEdit);
        swipeRevealLayout = view.findViewById(R.id.swipelayout);

        UserList.setAdapter(userListAdapter);

        createEntryViewModel = new ViewModelProvider(getActivity()).get(CreateEntryViewModel.class);
        alertDialogHelper = new AlertDialogHelper(getContext(), ViewUsersInRecyclerFragment.this);
        observeForDbChanges();
        observeQueryString();
        observeUsersDataList();
        observeMultiSelectStatus();

    }

    public void multi_select(int position, View view) {
        if (mActionMode != null) {
            if (multiselect_list.contains(currentUserList.get(position))) {
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_500));
                multiselect_list.remove(currentUserList.get(position));
            } else {
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.purple_200));
                multiselect_list.add(currentUserList.get(position));
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("0");
            refreshAdapter();

        }
    }

    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                currentUserList = multiselect_list;
                for (int i = 0; i < multiselect_list.size(); i++)
                    createEntryViewModel.deleteUserFromDatabase(currentUserList.get(i).getId());
                userListAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getActivity(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }

    @Override
    public void onNegativeClick(int from) {
        mActionModeCallback.onDestroyActionMode(mActionMode);
    }

    //Important
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        Menu menu;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            this.menu = menu;
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Are you sure want to delete all the selected contacts(s)?", "DELETE", "CANCEL", 1, false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<>();
            refreshAdapter();
        }
    };

    private void observeMultiSelectStatus() {
        createEntryViewModel.getIsMultiSelectOn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                multiSelectStatus = aBoolean;
            }
        });
    }

    public void refreshAdapter() {
        userListAdapter.selected_usersList = multiselect_list;
    }

    private void observeQueryString() {
        createEntryViewModel.getQueryString().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String query) {
                Log.d("TAG", "Inside Tab1Fragment: " + query);
                queryChatList(query);
            }
        });
    }

    private void queryChatList(String query) {
        query = "%" + query + "%";
        createEntryViewModel.queryInit(query);
        createEntryViewModel.queriedUserList.observe(this, new Observer<PagedList<User>>() {
            @Override
            public void onChanged(PagedList<User> users) {
                userListAdapter.submitList(users);
            }
        });
    }

    @Override
    public void onResume() {
        // observeUsersDataList();
        super.onResume();
        Log.e("TAG", "onResume: ");
    }

    private void observeUsersDataList() {
        createEntryViewModel.userList.observe(this, users -> userListAdapter.submitList(users));

    }

    private void observeForDbChanges() {
        createEntryViewModel.userList.observe(getViewLifecycleOwner(), new Observer<PagedList<User>>() {
            @Override
            public void onChanged(PagedList<User> users) {
                currentUserList = users.snapshot();
                userListAdapter.submitList(users);
            }
        });

    }

    @Override
    public void edit(int clickPosition) {
        ViewEditActivityCalling(Constants.editActionType, clickPosition);
    }

    private void ViewEditActivityCalling(String edit_view, int clickPosition) {
        addUserForEditDelete();
        Intent intent = new Intent(getActivity(), EditUserDetailActivity.class);
        intent.putExtra("ID", String.valueOf(userList.get(clickPosition).getId()));
        intent.putExtra(Constants.actionType, edit_view);
        getActivity().startActivity(intent);

    }

    private void addUserForEditDelete() {
        createEntryViewModel.userList.observe(getActivity(), users -> {
            if (users != null && users.size() > 0) {
                storeUser(users);
            }
        });
    }

    private void storeUser(List<User> users) {
        userList = new ArrayList<>();
        userList.addAll(users);
    }

    @Override
    public void delete(int clickPosition) {
        addUserForEditDelete();
        createEntryViewModel.deleteUserFromDatabase(userList.get(clickPosition).getId());
    }

    @Override
    public void imageClick(int clickPosition) {
        Intent intent = new Intent(getActivity(), DetailOfUserDisplay.class);
        intent.putExtra("ID", String.valueOf(userList.get(clickPosition).getId()));
        getActivity().startActivity(intent);
    }

    public void imageOnClick(int clickPosition) {
        Intent intent = new Intent(getActivity(), DetailOfUserDisplay.class);
        intent.putExtra("ID", String.valueOf(userList.get(clickPosition).getId()));
        getActivity().startActivity(intent);
    }


}