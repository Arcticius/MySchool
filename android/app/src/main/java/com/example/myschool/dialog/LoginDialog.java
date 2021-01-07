package com.example.myschool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myschool.util.EditTextWithClear;
import com.example.myschool.R;
import com.example.myschool.activity.AdminActivity;
import com.example.myschool.activity.StudentActivity;
import com.example.myschool.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class LoginDialog extends Dialog {

    private Button loginButton;
    private Button cancelButton;
    private RadioGroup radioGroup;
    private RadioButton adminRadio;
    private RadioButton studentRadio;
    private int radioId;

    private String url;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private StringRequest stringRequest;
    private String passwordReturned="";
    private String msg = "";
    private String access = "";
    private String uid = "";
    private EditTextWithClear usernameText, passwordText;
    private SharedPreferences sharedPreferences;

    public LoginDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);

        loginButton = (Button)findViewById(R.id.button_login);
        cancelButton = (Button)findViewById(R.id.button_cancel);
        radioGroup = (RadioGroup)findViewById(R.id.login_radio_group);
        adminRadio = (RadioButton)findViewById(R.id.radio_admin);
        studentRadio = (RadioButton)findViewById(R.id.radio_stu);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDialog.this.dismiss();
            }
        });
    }

    public void login(){
        radioId = radioGroup.getCheckedRadioButtonId();

        usernameText = (EditTextWithClear)findViewById(R.id.name);
        passwordText = (EditTextWithClear)findViewById(R.id.pwd);
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        }
        catch(JSONException e){
            e.printStackTrace();
        }


        //url = "http://192.168.43.46:8080/login/" + username;  //string方法
        url = "http://192.168.111.1:8080/login";    //json方法

        if(username.equals("") || password.equals(""))
            Toast.makeText(getContext(), "Please enter both username and password.", Toast.LENGTH_SHORT).show();
        else{
            sendLoginRequest(jsonObject);
        }
    }

    // 使用StringRequest实现HTTP通信
    public void sendAndRequestResponse(){

        requestQueue = Volley.newRequestQueue(getContext());
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    System.out.println("onResponse");
                    Log.d("TAG", response);
                    JSONObject jsonObject = new JSONObject(response);
                    passwordReturned = jsonObject.getString("password");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", "ERROR: " + error.toString());
            }
        });

        requestQueue.add(stringRequest);
    }

    //使用JsonObjectRequest实现通信
    public void sendLoginRequest(JSONObject jsonObject){

        requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    System.out.println("onResponse");
                    Log.d("TAG", response.toString());
                    msg = response.getString("msg");
                    access = response.getString("access");

                    if(msg.equals("success")){  //用户名和密码匹配
                        if(access.equals("1") && radioId == adminRadio.getId()){ //用户是管理员
                            Toast.makeText(getContext().getApplicationContext(), "Login succeeded", Toast.LENGTH_SHORT).show();

                            JSONArray studentsJsonArray = response.getJSONArray("detail");

                            //存储学生数据，供listview初始化使用
                            List<User> students = toList(studentsJsonArray);
                            System.out.println(students.get(0).toString());

//                            sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("uid", detail.getString("uid"));
//                            editor.commit();

                            Intent intent = new Intent(getContext(), AdminActivity.class);
                            intent.putExtra("students", (Serializable)students);
                            getContext().startActivity(intent);
                            dismiss();
                        }
                        else if(access.equals("0") && radioId == studentRadio.getId()){//用户是学生
                            Toast.makeText(getContext().getApplicationContext(), "Login succeeded", Toast.LENGTH_SHORT).show();

                            //存储学生数据
                            JSONObject detail = response.getJSONObject("detail");
                            sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("uid", detail.getString("uid"));
                            editor.putString("username", detail.getString("username"));
                            editor.putString("batch", detail.getString("batch"));
                            editor.putString("dob", detail.getString("dob"));
                            editor.putString("email", detail.getString("email"));
                            editor.putString("bloodGroup", detail.getString("bloodGroup"));
                            editor.putString("contactNumber", detail.getString("contactNumber"));
                            editor.putString("address", detail.getString("address"));
                            editor.putString("leaveBalance", String.valueOf(detail.getInt("leaveBalance")));

                            editor.commit();

                            Intent intent = new Intent(getContext(), StudentActivity.class);
                            getContext().startActivity(intent);
                            dismiss();
                        }
                        else    //用户身份不匹配
                            Toast.makeText(getContext().getApplicationContext(), "Invalid permission", Toast.LENGTH_SHORT).show();
                    }
                    else{   //用户名和密码不匹配
                        Toast.makeText(getContext().getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", "ERROR: " + error.toString());
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
