package com.example.assignment.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.assignment.EditAndDeleteInterface;
import com.example.assignment.R;
import com.example.assignment.ItemClickListener;
import com.example.assignment.activities.DetailOfUserDisplay;
import com.example.assignment.activities.EditUserDetailActivity;
import com.example.assignment.activities.MainActivity;
import com.example.assignment.models.User;
import com.example.assignment.viewmodel.CreateEntryViewModel;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListAdapter extends PagedListAdapter<User, UserListAdapter.MyViewHolder> {
    EditAndDeleteInterface editAndDeleteInterface;
    static String id;
    Activity activity;
    public ArrayList<User> userArrayList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    public ArrayList<User> selected_usersList = new ArrayList<>();
    public static ItemCallback<User> DIFF__CALLBACK = new ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.equals(newItem);
        }
    };

    public UserListAdapter(EditAndDeleteInterface itemClickListener, Activity activity) {
        super(DIFF__CALLBACK);
        //  this.itemClickListener = itemClickListener;
        this.editAndDeleteInterface = itemClickListener;
        viewBinderHelper.setOpenOnlyOne(true);
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = getItem(position);
        if (user != null) {
            holder.userName.setText(user.getName());
        }
        Log.d("image r", String.valueOf(user.getImage()));
        Log.d("image o", String.valueOf(R.drawable.ic_baseline_person_24));

        if (user.getImage() == null || ("").equalsIgnoreCase(user.getImage()))
            holder.userImage.setImageResource(R.drawable.ic_baseline_person_24);
        else
            holder.userImage.setImageURI(Uri.parse(user.getImage()));

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.closeLayout(user.getName());
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(user.getId()));
        holder.txtEdit.setTag(position);
        holder.txtDelete.setTag(position);
        holder.userImage.setTag(position);
//        holder.userImage.
        holder.userImage.setOnClickListener(v -> {
            int clickPosition = (int) holder.txtEdit.getTag();
            editAndDeleteInterface.imageClick(clickPosition);
        });

        holder.txtEdit.setOnClickListener(v -> {
            int clickPosition = (int) holder.txtEdit.getTag();
            editAndDeleteInterface.edit(clickPosition);
        });
        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickPosition = (int) holder.txtDelete.getTag();
                editAndDeleteInterface.delete(clickPosition);
            }
        });
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "on Click image", Toast.LENGTH_LONG).show();
            }
        });

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_user)
        TextView userName;
        //  @BindView(R.id.image_user)
        ImageView userImage;
        @BindView(R.id.checkbox)
        ImageView checkbox;
        //        @BindView(R.id.txtEdit)
        Button txtEdit;
        //        @BindView(R.id.txtDelete)
        Button txtDelete;
        //        @BindView(R.id.swipelayout)
        SwipeRevealLayout swipeRevealLayout;
        LinearLayout llUserRecycler;
        CardView card_view;
        View v;

        CreateEntryViewModel createEntryViewModel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEdit = itemView.findViewById(R.id.txtEdit);
            txtDelete = itemView.findViewById(R.id.txtDelete);
            userImage = itemView.findViewById(R.id.image_user);
            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);
            card_view = itemView.findViewById(R.id.card_view);
            llUserRecycler = itemView.findViewById(R.id.llUserRecycler);
            ButterKnife.bind(this, itemView);
            this.v = itemView;
        }


    }

}