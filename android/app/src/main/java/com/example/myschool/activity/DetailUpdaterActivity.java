package com.example.myschool.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myschool.util.EditTextWithClear;
import com.example.myschool.R;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailUpdaterActivity extends AppCompatActivity {

    private Button cancelButton, saveButton;
    private EditTextWithClear nameText, dobText, bloodText, emailText, phoneText, addressText;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url;

    private String name, dob, blood, email, phone, address;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_updater);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        //初始化
        initViews();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    public void initViews(){
        cancelButton = (Button)findViewById(R.id.button_cancel);
        saveButton = (Button)findViewById(R.id.button_save);
        nameText = (EditTextWithClear)findViewById(R.id.text_update_name);
        dobText = (EditTextWithClear)findViewById(R.id.text_update_dob);
        bloodText = (EditTextWithClear)findViewById(R.id.text_update_blood);
        emailText = (EditTextWithClear)findViewById(R.id.text_update_email);
        phoneText = (EditTextWithClear)findViewById(R.id.text_update_phone);
        addressText = (EditTextWithClear)findViewById(R.id.text_update_address);

        nameText.setText(sharedPreferences.getString("username", ""));
        dobText.setText(sharedPreferences.getString("dob", ""));
        bloodText.setText(sharedPreferences.getString("bloodGroup", ""));
        emailText.setText(sharedPreferences.getString("email", ""));
        phoneText.setText(sharedPreferences.getString("contactNumber", ""));
        addressText.setText(sharedPreferences.getString("address", ""));
    }

    //更新
    public void update(){
        name = nameText.getText().toString();
        dob = dobText.getText().toString();
        blood = bloodText.getText().toString();
        email = emailText.getText().toString();
        phone = phoneText.getText().toString();
        address = addressText.getText().toString();

        if(!existsBlank(name, dob, blood, email, phone, address)){
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("username", name);
                jsonObject.put("dob", dob);
                jsonObject.put("bloodGroup", blood);
                jsonObject.put("email", email);
                jsonObject.put("contactNumber", phone);
                jsonObject.put("address", address);

                Intent intent = getIntent();
                jsonObject.put("uid", intent.getStringExtra("uid"));
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            url = "http://192.168.111.1:8080/update";
            sendUpdateRequest(jsonObject);
        }
        else{
            Toast.makeText(this, "You need to enter all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    //是否全部填写
    public boolean existsBlank(String name, String dob, String blood, String email, String phone, String address){
        return (name.equals("")) || (dob.equals("")) || (blood.equals("")) || (email.equals("")) || (phone.equals("")) || (address.equals(""));
    }

    public void sendUpdateRequest(JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(DetailUpdaterActivity.this, "Success", Toast.LENGTH_SHORT).show();

                updateStudentData(jsonObject);//更新shared preferences中的学生信息

                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("ERROR", volleyError.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    //更新shared preferences中的学生信息
    public void updateStudentData(JSONObject jsonObject){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try{
            JSONObject detail = jsonObject.getJSONObject("detail");

            editor.putString("uid", detail.getString("uid"));
            editor.putString("username", detail.getString("username"));
            editor.putString("batch", detail.getString("batch"));
            editor.putString("dob", detail.getString("dob"));
            editor.putString("email", detail.getString("email"));
            editor.putString("bloodGroup", detail.getString("bloodGroup"));
            editor.putString("contactNumber", detail.getString("contactNumber"));
            editor.putString("address", detail.getString("address"));

            editor.commit();
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
}
