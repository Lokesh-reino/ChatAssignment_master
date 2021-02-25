package com.example.assignment.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.example.assignment.R;
import com.example.assignment.adapters.MyFragmentAdapter;
import com.example.assignment.helpers.UriFromBitmap;
import com.example.assignment.models.User;
import com.example.assignment.repository.LocalRepository;
import com.example.assignment.viewmodel.CreateEntryViewModel;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;

public class CreateEntryFragment extends Fragment {

    private CreateEntryViewModel mViewModel;
    @BindView(R.id.image_view_user)
    ImageView userImage;

    @BindView(R.id.editTextDOB)
    EditText userBirthDay;

    @BindView(R.id.editTextPersonName)
    EditText userName;

    @BindView(R.id.editTextPhone)
    EditText userPhoneNumber;

    @BindView(R.id.button_save)
    Button button;
    //    @BindView(R.id.date_picker)
//    TextView datePick;
    @BindView(R.id.ivAddProfilePic)
    CircleImageView addProfilePic;

    String ProfilePicPath;
    String ProfilePicUri;

    private final int REQUEST_CODE_CAMERA = 0;
    private final int REQUEST_CODE_GALLERY = 1;
    private Long createdDate;
    private Long modifiedDate;
    SimpleDateFormat sdf = new SimpleDateFormat("MM dd,yyyy HH:mm");
    UriFromBitmap uriFromBitmap;

    public static CreateEntryFragment newInstance() {
        return new CreateEntryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_entry_layout, container, false);
        ButterKnife.bind(this, view);
        uriFromBitmap = new UriFromBitmap();


        userBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        userBirthDay.setText(date);
                    }
                },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String phoneNumber = userPhoneNumber.getText().toString();
                String birthday = userBirthDay.getText().toString();
                createdDate = System.currentTimeMillis();
                modifiedDate = System.currentTimeMillis();
                Date date = new Date(createdDate);

                User user = new User(name, phoneNumber, birthday, ProfilePicUri);
                mViewModel.saveToDatabase(user);
                Toast.makeText(getActivity(), "Successfully Resisted", Toast.LENGTH_SHORT).show();
                clearFields();

                changeTab();
            }
        });

        addProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(getContext());

            }
        });

        return view;
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (options[item].equals("Take Photo")) {

                    checkPermissionAndStartCamera();

                } else if (options[item].equals("Choose from Gallery")) {

                    checkPermissionAndOpenGallery();

                } else
                    dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_GALLERY);
        else
            startOpenGalleryIntent();
    }

    private void startOpenGalleryIntent() {
        Intent intentPickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentPickPhoto, REQUEST_CODE_GALLERY);
    }

    private void startTakePictureIntent() {
        Intent intentTakePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentTakePicture, REQUEST_CODE_CAMERA);
    }

    private void checkPermissionAndStartCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        else
            startTakePictureIntent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startTakePictureIntent();
            else
                Toast.makeText(getContext(), "Failed. Please grant camera permission", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                startOpenGalleryIntent();
            else
                Toast.makeText(getContext(), "Failed. Please grant gallery permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    Bitmap bitmapCameraImage = (Bitmap) data.getExtras().get("data");
                    Uri cameraImageUri = null;
                    try {
                        Log.d("TAG", "Inside try of onActivity result of Tab2Fragment");

                        cameraImageUri = uriFromBitmap.getImageUri(getContext(), bitmapCameraImage);
                        Log.d("TAG", "cameraUri: " + cameraImageUri.toString());
                        Log.d("TAG", "cameraUri: " + cameraImageUri.getPath());

                    } catch (Exception e) {
                        Log.d("TAG", "Inside catch: " + e.getMessage());
                        e.printStackTrace();
                    }
                    userImage.setImageURI(cameraImageUri);
                    ProfilePicPath = cameraImageUri.getPath();
                    ProfilePicUri = cameraImageUri.toString();
                    break;

                case REQUEST_CODE_GALLERY:
                    Uri selectedImageUri = data.getData();
                    Log.d("TAG", "URi: " + selectedImageUri.getPath());
                    ProfilePicPath = selectedImageUri.getPath();
                    ProfilePicUri = selectedImageUri.toString();
                    userImage.setImageURI(selectedImageUri);
                    break;
            }
        }
    }


    private void changeTab() {
        ViewPager viewPager = getActivity().findViewById(R.id.view_pager);
        viewPager.setCurrentItem(0, true);
    }

    private void clearFields() {
        userImage.setImageResource(R.drawable.ic_baseline_person_24);
        userName.setText("");
        userPhoneNumber.setText("");
        userBirthDay.setText("");
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(CreateEntryViewModel.class);
    }

}