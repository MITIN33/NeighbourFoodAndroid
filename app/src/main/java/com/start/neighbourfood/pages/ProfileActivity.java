package com.start.neighbourfood.pages;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.start.neighbourfood.NFApplication;
import com.start.neighbourfood.R;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.tasks.DownLoadImageTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends BaseActivity {


    private static final int SELECT_PICTURE = 100;
    private final String TAG = "PROFILE_ACTIVITY";
    private UserBaseInfo userBaseInfo;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button saveButton = findViewById(R.id.save_profile);
        Button cancelButton = findViewById(R.id.cancel_profile);

        userBaseInfo = NFApplication.getSharedPreferenceUtils().getUserBaseInfo();

        final EditText fName = findViewById(R.id.profile_first_name);
        final EditText lName = findViewById(R.id.profile_last_name);
        final ImageView profilePic = findViewById(R.id.profile_pic);
        final EditText apartment = findViewById(R.id.apartment_name);
        final EditText flatNumber = findViewById(R.id.flat_Number_profile);
        final EditText phone = findViewById(R.id.mobile_number_profile);
        //set initial info
        fName.setText(userBaseInfo.getfName());
        lName.setText(userBaseInfo.getlName());
        apartment.setText(userBaseInfo.getApartmentName());
        flatNumber.setText(userBaseInfo.getFlatNumber());
        phone.setText(userBaseInfo.getPhoneNo());
        if (userBaseInfo.getPhotoUrl() != null) {
            new DownLoadImageTask(profilePic).execute(userBaseInfo.getPhotoUrl());
        }


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fName.getText().toString().equals(userBaseInfo.getfName()) || !lName.getText().toString().equals(userBaseInfo.getlName())
                    || selectedImageUri != null) {
                    showProgressDialog();
                    userBaseInfo.setfName(fName.getText().toString());
                    userBaseInfo.setlName(lName.getText().toString());
                    checkForImageChange();
                } else {
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkForImageChange() {
        if (selectedImageUri != null){
            uploadImageTofirebase();
        }
        else {
            updateDb();
        }
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri

                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);
                    ((ImageView) findViewById(R.id.profile_pic)).setImageURI(selectedImageUri);
                }
            }
        }
    }

    private void uploadImageTofirebase(){
        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Images").child(selectedImageUri.getLastPathSegment());
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] byteArray = baos.toByteArray();

        filepath.putBytes(byteArray).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri downloadUri = task.getResult();
                    userBaseInfo.setPhotoUrl(downloadUri.toString());
                    updateDb();
                    Log.i(TAG, "Image Updated");
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void updateDb() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("photoUrl", userBaseInfo.getPhotoUrl());
            jsonObject.put("fname", userBaseInfo.getfName());
            jsonObject.put("lname", userBaseInfo.getlName());
            serviceManager.updateUserInfo(userBaseInfo.getUserUid(), jsonObject, new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject request, JSONObject result) {
                    sharedPreferenceUtils.setValue(ServiceConstants.USER_INFO, userBaseInfo);
                    hideProgressDialog();
                    finish();
                }

                @Override
                public void onErrorResponse(JSONObject request, VolleyError error) {
                    hideProgressDialog();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Set the image in ImageView

    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
