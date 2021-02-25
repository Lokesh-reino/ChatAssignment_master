package com.example.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;

import com.example.assignment.activities.ViewUsersInRecyclerFragment;

public class AlertDialogHelper {
    Context context;
    AlertDialog alertDialog = null;
    AlertDialogListener callBack;
    Activity current_activity;
    ViewUsersInRecyclerFragment chatListFragment;

    public AlertDialogHelper(Context context, ViewUsersInRecyclerFragment chatListFragment) {
        this.context = context;
        this.current_activity = (Activity) context;
        this.chatListFragment = chatListFragment;
        callBack = (AlertDialogListener) chatListFragment;
    }

    public void showAlertDialog(String title, String message, String positive, String negative, final int from, boolean isCancelable) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#FF0000'>" + positive + "</font>"),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        callBack.onPositiveClick(from);
                        alertDialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton(negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onNegativeClick(from);
                        alertDialog.dismiss();
                    }
                });

        alertDialogBuilder.setCancelable(isCancelable);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public interface AlertDialogListener {
        void onPositiveClick(int from);

        void onNegativeClick(int from);

    }
}
