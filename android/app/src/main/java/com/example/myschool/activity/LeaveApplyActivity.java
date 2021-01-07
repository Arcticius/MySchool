package com.example.myschool.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class LeaveApplyActivity extends Activity {

    private Button saveButton, cancelButton, newButton;
    private LinearLayout applyLayout, historyLayout;
    private TextView historyName, historyDate, historyReason, historyStatus, noticingText;
    private EditTextWithClear nameText, dateText, reasonText;
    private String name, date, reason;
    private SharedPreferences sharedPreferences;

    private String mode;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_apply);

        sharedPreferences = getSharedPreferences("leave_data", MODE_PRIVATE);

        //初始化
        initViews();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crossFade();
            }
        });

        //新建请假申请
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewApply();
            }
        });
    }

    public void initViews(){
        newButton = (Button)findViewById(R.id.button_apply_start_new);
        cancelButton = (Button)findViewById(R.id.button_apply_cancel);
        saveButton = (Button)findViewById(R.id.button_apply_save);
        historyLayout = (LinearLayout)findViewById(R.id.my_history);
        historyName = (TextView)findViewById(R.id.text_history_name);
        historyDate = (TextView)findViewById(R.id.text_history_date);
        historyReason = (TextView)findViewById(R.id.text_history_reason);
        historyStatus = (TextView)findViewById(R.id.text_history_status);
        noticingText = (TextView)findViewById(R.id.text_apply_noticing);
        applyLayout = (LinearLayout)findViewById(R.id.apply_leave);
        nameText = (EditTextWithClear)findViewById(R.id.text_apply_name);
        dateText = (EditTextWithClear)findViewById(R.id.text_apply_date);
        reasonText = (EditTextWithClear)findViewById(R.id.text_apply_reason);

        mode = sharedPreferences.getString("mode", "default");
        if(mode.equals("empty"))    //没有审核中或已拒绝的请求，显示输入框
            historyLayout.setVisibility(View.GONE);
        else{
            applyLayout.setVisibility(View.GONE);
            historyName.setText(sharedPreferences.getString("username", ""));
            historyDate.setText(sharedPreferences.getString("leaveDate", ""));
            historyReason.setText(sharedPreferences.getString("reason", ""));

            if(mode.equals("auditing")){   //存在正在审核的请求
                historyStatus.setText("Auditing");
                historyStatus.setTextColor(getResources().getColor(R.color.yellow));
                newButton.setVisibility(View.GONE);
            }
            else if(mode.equals("denied")){ //存在已拒绝的请求
                historyStatus.setText("Denied");
                historyStatus.setTextColor(getResources().getColor(R.color.red));
                noticingText.setVisibility(View.GONE);
            }
        }

    }

    //渐变动画
    public void crossFade(){
        applyLayout.setAlpha(0f);
        applyLayout.setVisibility(View.VISIBLE);
        applyLayout.animate()
                .alpha(1f)
                .setDuration(5000)
                .setListener(null);

        historyLayout.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        historyLayout.setVisibility(View.GONE);
                    }
                });
    }

    //新建请假申请
    public void addNewApply(){
        name = nameText.getText().toString();
        date = dateText.getText().toString();
        reason = reasonText.getText().toString();

        if(!existsBlank(name, date, reason)){
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("uid", sharedPreferences.getString("uid", ""));
                jsonObject.put("leaveDate", date);
                jsonObject.put("reason", reason);
                jsonObject.put("status", "auditing");
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            url = "http://192.168.111.1:8080/addApply";
            System.out.println(jsonObject.toString());
            sendAddApplyRequest(jsonObject);
        }
        else{
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean existsBlank(String name, String date, String reason){
        return (name.equals("")) || (date.equals("")) || (reason.equals(""));
    }

    public void sendAddApplyRequest(final JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(this);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());
                Toast.makeText(LeaveApplyActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                try{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("leaveDate", myJsonObject.getString("leaveDate"));
                    editor.putString("reason", myJsonObject.getString("reason"));
                    editor.putString("mode", "auditing");

                    editor.commit();
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

                //刷新页面
                onCreate(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("ERROR", volleyError.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
