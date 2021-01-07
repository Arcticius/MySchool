package com.example.myschool.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.example.myschool.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private EditTextWithClear uidText, usernameText, passwordText, batchText, dobText, leaveText, bloodText, emailText, phoneText, addressText, ageText;
    private Button cancelButton, saveButton;

    private String uid, username, password, batch, dob, blood, email, phone, address, leave, age;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url;
    private List<User> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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
                addStudent();
            }
        });
    }

    public void initViews(){
        uidText = (EditTextWithClear)findViewById(R.id.text_add_uid);
        usernameText = (EditTextWithClear)findViewById(R.id.text_add_name);
        passwordText = (EditTextWithClear)findViewById(R.id.text_add_pwd);
        batchText = (EditTextWithClear)findViewById(R.id.text_add_batch);
        dobText = (EditTextWithClear)findViewById(R.id.text_add_dob);
        leaveText = (EditTextWithClear)findViewById(R.id.text_add_balance);
        bloodText = (EditTextWithClear)findViewById(R.id.text_add_blood);
        emailText = (EditTextWithClear)findViewById(R.id.text_add_email);
        phoneText = (EditTextWithClear)findViewById(R.id.text_add_phone);
        addressText = (EditTextWithClear)findViewById(R.id.text_add_address);
        cancelButton = (Button)findViewById(R.id.button_add_cancel);
        saveButton = (Button)findViewById(R.id.button_add_save);
    }

    public void addStudent(){
        uid = uidText.getText().toString();
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        batch = batchText.getText().toString();
        dob = dobText.getText().toString();
        leave = leaveText.getText().toString();
        blood = bloodText.getText().toString();
        email = emailText.getText().toString();
        phone = phoneText.getText().toString();
        address = addressText.getText().toString();

        if(!existsBlank(uid, username, password, batch, dob, leave, blood, email, phone, address)){
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("uid", uid);
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("access", 0);
                jsonObject.put("batch", batch);
                jsonObject.put("age", 20);
                jsonObject.put("dob", dob);
                jsonObject.put("bloodGroup", blood);
                jsonObject.put("address", address);
                jsonObject.put("contactNumber", phone);
                jsonObject.put("email", email);
                jsonObject.put("leaveBalance", Integer.valueOf(leave));
            }
            catch (JSONException e){
                e.printStackTrace();
            }

            url = "http://192.168.111.1:8080/add";

            sendAddRequest(jsonObject);
        }
        else
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
    }

    public boolean existsBlank(String uid, String username, String password, String batch, String dob, String leave, String blood, String email, String phone, String address){

        return (uid.equals("")) || (username.equals("")) || (password.equals("")) || (batch.equals(""))
                || (dob.equals("")) || (leave.equals("")) || (blood.equals("")) || (email.equals(""))
                || (phone.equals("")) || (address.equals(""));
    }

    public void sendAddRequest(JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());

                Toast.makeText(AddActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();

                try{
                    JSONArray studentsJSONArray = jsonObject.getJSONArray("detail");
                    students = toList(studentsJSONArray);

                    Intent intent = new Intent(AddActivity.this, AdminActivity.class);
                    intent.putExtra("students", (Serializable) students);
                    startActivity(intent);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("ERROR", volleyError.toString());
                Toast.makeText(AddActivity.this, "Failed to add new student!", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    //JSONArray 转 List
    public List<User> toList(JSONArray studentsJsonArray){
        List<User> students = new ArrayList<>();
        try{
            for(int i = 0; i < studentsJsonArray.length(); i++){
                User student = new User();
                JSONObject studentJson = studentsJsonArray.getJSONObject(i);

                student.setUid(studentJson.getString("uid"));
                student.setUsername(studentJson.getString("username"));
                student.setPassword(studentJson.getString("password"));
                student.setAccess(studentJson.getString("access"));
                student.setBatch(studentJson.getString("batch"));
                student.setAge(studentJson.getInt("age"));
                student.setDob(studentJson.getString("dob"));
                student.setBloodGroup(studentJson.getString("bloodGroup"));
                student.setAddress(studentJson.getString("address"));
                student.setContactNumber(studentJson.getString("contactNumber"));
                student.setEmail(studentJson.getString("email"));
                student.setLeaveBalance(studentJson.getInt("leaveBalance"));

                students.add(student);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return students;
    }
}
