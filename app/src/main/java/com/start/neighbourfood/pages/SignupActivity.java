package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.start.neighbourfood.R;
import com.start.neighbourfood.Utils.JSONHelper;
import com.start.neighbourfood.auth.TaskHandler;
import com.start.neighbourfood.models.ApartmentsInfo;
import com.start.neighbourfood.models.ServiceConstants;
import com.start.neighbourfood.models.v1.FlatInfo;
import com.start.neighbourfood.models.v1.UserBaseInfo;
import com.start.neighbourfood.services.ServiceManager;
import com.start.neighbourfood.taskhandlers.SignInTaskHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class SignupActivity extends BaseActivity {

    private final String TAG = "SIGNIN_ACTIVITY";
    private ServiceManager serviceManager;
    private Spinner spinner, flatNumber;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        serviceManager = ServiceManager.getInstance(this);
        spinner = (Spinner) findViewById(R.id.apartment_dropdown_list);
        flatNumber = findViewById(R.id.flat_number_signup);
        checkBox = findViewById(R.id.signup_checkbox);
        // Create an ArrayAdapter using the string array and a default spinner layout
        fetchApartmentList();

        EditText editText = (EditText) findViewById(R.id.mobile_number);
        String phone = (String) getIntent().getExtras().get(ServiceConstants.PHONE_NUMBER);
        if (!TextUtils.isEmpty(phone)) {
            editText.setText(phone);
        } else {
            editText.setEnabled(true);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ApartmentsInfo apartmentsInfo = (ApartmentsInfo) adapterView.getAdapter().getItem(i);
                fetchFlatsforApartments(apartmentsInfo.getApartmentID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long apartmentNo = ((Spinner) findViewById(R.id.apartment_dropdown_list)).getSelectedItemId();
                EditText fName = ((EditText) findViewById(R.id.first_name));
                EditText lName = ((EditText) findViewById(R.id.last_name));
                EditText phoneNo = ((EditText) findViewById(R.id.mobile_number));

                if (apartmentNo == -1 || flatNumber.getSelectedItem() == null || TextUtils.isEmpty(flatNumber.getSelectedItem().toString())
                        || TextUtils.isEmpty(fName.getText())
                        || TextUtils.isEmpty(lName.getText())
                        || TextUtils.isEmpty(phoneNo.getText())
                        || phoneNo.getText().length() != 10) {
                    Toast.makeText(SignupActivity.this, "Fill the required field properly.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkBox.isChecked()) {
                    Toast.makeText(SignupActivity.this, "Please click the checkbox.", Toast.LENGTH_SHORT).show();
                    return;
                }
                addUserToDB();
            }
        });
    }

    private void fetchFlatsforApartments(String apartmentID) {
        ServiceManager.getInstance(this).fetchAllFlatsInApartment(apartmentID, new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject request, JSONObject result) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<FlatInfo> list = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<FlatInfo>>() {
                    });
                    ArrayAdapter<FlatInfo> adapter = new ArrayAdapter<FlatInfo>(SignupActivity.this, android.R.layout.simple_spinner_item, list);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    flatNumber.setAdapter(adapter);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
            }

            @Override
            public void onErrorResponse(JSONObject request, VolleyError error) {
                hideProgressDialog();
            }
        });
    }

    private void fetchApartmentList() {
        showProgressDialog();
        serviceManager.fetchAllApartments(new TaskHandler() {
            @Override
            public void onTaskCompleted(JSONObject request, JSONObject result) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<ApartmentsInfo> list = objectMapper.readValue(result.getJSONArray("Result").toString(), new TypeReference<List<ApartmentsInfo>>() {
                    });
                    ArrayAdapter<ApartmentsInfo> adapter = new ArrayAdapter<ApartmentsInfo>(SignupActivity.this, android.R.layout.simple_spinner_item, list);
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    spinner.setAdapter(adapter);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
            }

            @Override
            public void onErrorResponse(JSONObject request, VolleyError error) {
                hideProgressDialog();
            }
        });
    }

    private void addUserToDB() {
        showProgressDialog();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        try {
            userBaseInfo.setUserUid(user.getUid());
            userBaseInfo.setApartmentId((((ApartmentsInfo) spinner.getSelectedItem()).getApartmentID()));
            userBaseInfo.setFlatID(((FlatInfo) flatNumber.getSelectedItem()).getFlatID());
            userBaseInfo.setfName(((EditText) findViewById(R.id.first_name)).getText().toString());
            userBaseInfo.setlName(((EditText) findViewById(R.id.last_name)).getText().toString());
            userBaseInfo.setPhoneNo(((EditText) findViewById(R.id.mobile_number)).getText().toString());
            userBaseInfo.setUserName(user.getDisplayName());
            userBaseInfo.setRating("4.5");
            serviceManager.createUser(JSONHelper.toJSONObject(userBaseInfo), new SignInTaskHandler(this));
        } catch (Exception e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }
}
