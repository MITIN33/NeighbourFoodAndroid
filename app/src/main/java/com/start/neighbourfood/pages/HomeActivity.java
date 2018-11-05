package com.start.neighbourfood.pages;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.start.neighbourfood.R;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.fragments.FlatListFragment;
import com.start.neighbourfood.fragments.SellerFoodFragment;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.services.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = "HOME_ACTIVITY";
    private UserBaseInfo user;

    private static final int SELECT_PICTURE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = getUserBaseInfo();
        // Check for login
        if (user == null || getFromSharedPreference(ServiceConstants.signedInKey) == null) {
            navigateToLoginPage();
            return;
        }

        registerDevice(user.getUserUid(), getFromSharedPreference("regId"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.header_username);
        ImageView imageView = headerView.findViewById(R.id.header_imageView);
        TextView emailView = headerView.findViewById(R.id.header_mail);

        userName.setText(String.format("%s %s", user.getfName(), user.getlName()));
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            new DownLoadImageTask(imageView).execute(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        }
        else {
            imageView.setImageResource(R.drawable.food_icon);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        emailView.setText("+91 " + user.getPhoneNo());


    }

    private void registerDevice(String uid, String regId) {
        String savedId = getFromSharedPreference("deviceRegistered");
        if (regId != null && (savedId == null || "false".equals(getFromSharedPreference("deviceRegistered")))) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userUid", uid);
                jsonObject.put("tokenId", regId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ServiceManager.getInstance(getApplicationContext()).addUserTokenInfo(jsonObject, new TaskHandler() {
                @Override
                public void onTaskCompleted(JSONObject result) {
                    saveStringInSharedPreference("deviceRegistered", "true");
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error Saving: " + error);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();
        } else {
            if (id == R.id.nav_settings) {

            } else if (id == R.id.nav_trackOrder) {
                navigateToOrderHistory();
            } else if (id == R.id.nav_home) {
                loadFragment(new FlatListFragment());
            } else if (id == R.id.nav_logout) {
                signOut();
            } else if (id == R.id.nav_share) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "Check it out. Your message goes here";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));

            } else if (id == R.id.seller_page) {
                loadFragment(new SellerFoodFragment());
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFromSharedPreference(ServiceConstants.userDetail) == null){
            return;
        }
        if (isNetworkConnected()) {
            clearBackstack();
            loadFragment(new FlatListFragment());
        } else {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    public void clearBackstack() {

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }

    }

    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri

                    String path = getPathFromURI(selectedImageUri);
                    Log.i(TAG, "Image Path : " + path);

                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Images").child(selectedImageUri.getLastPathSegment());

                    filepath.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUri)
                                        .build();

                                FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("photoUrl",downloadUri);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                ServiceManager.getInstance(HomeActivity.this).updateProfilePhoto(user.getUserUid(), downloadUri.toString(), jsonObject,new TaskHandler() {
                                                    @Override
                                                    public void onTaskCompleted(JSONObject result) {

                                                    }

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                });
                                            }
                                        });

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });

                    // Set the image in ImageView
                    ((ImageView) findViewById(R.id.header_imageView)).setImageURI(selectedImageUri);
                }
            }
        }
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

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
