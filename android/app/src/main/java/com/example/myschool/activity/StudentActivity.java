package com.example.myschool.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myschool.R;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentActivity extends AppCompatActivity {

    private Button updateButton, applyButton;
    private TextView batchText, rollText, leaveDaysText, nameText, dobText, bloodText, emailText, phoneText, addressText, daysText;
    private SharedPreferences sharedPreferences;
    private String uid;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url;
    private String msg, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        //初始化view
        initViews();

        //从shared preferences中获取学生信息
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        setStudentInfo();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, DetailUpdaterActivity.class);
                intent.putExtra("uid", sharedPreferences.getString("uid", ""));
                startActivity(intent);
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String leaveBalance = sharedPreferences.getString("leaveBalance", "");
                if(!leaveBalance.equals("0"))
                    checkForApply();
                else
                    Toast.makeText(StudentActivity.this, "You have run out of your leave balance!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //初始化view
    public void initViews(){
        updateButton = (Button)findViewById(R.id.button_update);
        applyButton = (Button)findViewById(R.id.button_apply_leave);
        batchText = (TextView)findViewById(R.id.text_batch);
        rollText = (TextView)findViewById(R.id.text_rollno);
        leaveDaysText = (TextView)findViewById(R.id.text_days);
        nameText = (TextView)findViewById(R.id.text_name);
        dobText = (TextView)findViewById(R.id.text_dob);
        bloodText = (TextView)findViewById(R.id.text_blood);
        emailText = (TextView)findViewById(R.id.text_email);
        phoneText = (TextView)findViewById(R.id.text_phone);
        addressText = (TextView)findViewById(R.id.text_address);
    }

    //从shared preferences中获取学生信息
    public void setStudentInfo(){
        batchText.setText(sharedPreferences.getString("batch", ""));
        nameText.setText(sharedPreferences.getString("username", ""));
        dobText.setText(sharedPreferences.getString("dob", ""));
        bloodText.setText(sharedPreferences.getString("bloodGroup", ""));
        emailText.setText(sharedPreferences.getString("email", ""));
        phoneText.setText(sharedPreferences.getString("contactNumber", ""));
        addressText.setText(sharedPreferences.getString("address", ""));
        leaveDaysText.setText(sharedPreferences.getString("leaveBalance", "") + " days");
    }

    //读取该生申请信息
    public void checkForApply(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("uid", sharedPreferences.getString("uid", ""));
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        url = "http://192.168.111.1:8080/checkApply";
        sendCheckApplyRequest(jsonObject);
    }

    public void sendCheckApplyRequest(final JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());
                try{
                    msg = jsonObject.getString("msg");
                    username = jsonObject.getString("access");
                    JSONObject detail = new JSONObject();

                    sharedPreferences = getSharedPreferences("leave_data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);

                    if(msg.equals("empty")){
                        editor.putString("mode", "empty");
                        editor.putString("uid", myJsonObject.getString("uid"));
                    }
                    else if(msg.equals("auditing")){
                        editor.putString("mode", "auditing");
                        detail = jsonObject.getJSONObject("detail");
                        editor.putString("uid", detail.getString("uid"));
                        editor.putString("leaveDate", detail.getString("leaveDate"));
                        editor.putString("reason", detail.getString("reason"));
                    }
                    else if(msg.equals("denied")){
                        editor.putString("mode", "denied");
                        detail = jsonObject.getJSONObject("detail");
                        editor.putString("uid", detail.getString("uid"));
                        editor.putString("leaveDate", detail.getString("leaveDate"));
                        editor.putString("reason", detail.getString("reason"));
                    }

                    editor.commit();

                    Intent intent = new Intent(StudentActivity.this, LeaveApplyActivity.class);
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
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();

        switch(item.getItemId()){
            case R.id.menu_logout:
                new AlertDialog.Builder(this).setTitle("Are you sure to log out?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                break;
            case R.id.menu_exit:
                new AlertDialog.Builder(this).setTitle("Are you sure to exit?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onDestroy();
                                finishAndRemoveTask();
                            }
                        }).show();

                break;
            default:
                break;
        }

        return true;
    }
}
