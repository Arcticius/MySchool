package com.example.myschool.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myschool.R;
import com.example.myschool.entity.LeaveListView;
import com.example.myschool.util.LeaveListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LeaveApprovalFrag extends Fragment implements AdapterView.OnItemSelectedListener{

    private ListView result;
    private ArrayList<LeaveListView> results = new ArrayList<>();

    private Button approveButton, rejectButton;
    private TextView uidText;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url;

    //准许按钮监听器
    private View.OnClickListener onApproveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button)v;
            try{
                JSONObject jsonObject = (JSONObject) button.getTag();
//                System.out.println(jsonObject.toString());
//                Toast.makeText(getContext(), jsonObject.getString("uid"), Toast.LENGTH_SHORT).show();
                jsonObject.put("status", "accepted");
                url = "http://192.168.111.1:8080/updateApply";
                sendUpdateApplyRequest(jsonObject);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    };

    //拒绝按钮监听器
    private View.OnClickListener onRejectButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button)v;
            try{
                JSONObject jsonObject = (JSONObject) button.getTag();
//                System.out.println(jsonObject.toString());
//                Toast.makeText(getContext(), jsonObject.getString("uid"), Toast.LENGTH_SHORT).show();
                jsonObject.put("status", "denied");
                url = "http://192.168.111.1:8080/updateApply";
                sendUpdateApplyRequest(jsonObject);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    };

    private class OnItemClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_leave_approval, container, false);

        result = (ListView)rootView.findViewById(R.id.leave_approval_list);
        result.setEmptyView(rootView.findViewById(R.id.text_approval_empty));

        readLeaveApply();  //读取所有处于审核状态的请假申请

//        result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                uidText = (TextView) view.findViewById(R.id.approval_uid);
//                Toast.makeText(getContext(), uidText.getText().toString(), Toast.LENGTH_SHORT).show();
//
//            }
//        });

        return rootView;
    }

    //更新请假申请表
    public void updateApplyList(JSONArray detail, JSONArray usernames){
        try{
            for(int i = 0; i < detail.length(); i++){
                LeaveListView leave = new LeaveListView();
                leave.setId(detail.getJSONObject(i).getInt("leaveId"));
                leave.setUid(detail.getJSONObject(i).getString("uid"));
                leave.setDate(detail.getJSONObject(i).getString("leaveDate"));
                leave.setReason(detail.getJSONObject(i).getString("reason"));
                leave.setName(usernames.getString(i));
                results.add(leave);
            }

            result.setAdapter(new LeaveListAdapter(getContext(), results, onApproveButtonClickListener, onRejectButtonClickListener));
            result.setOnItemClickListener(new OnItemClickHandler());
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        result.setAdapter(new LeaveListAdapter(getContext(), results, onApproveButtonClickListener, onRejectButtonClickListener));
        result.setOnItemClickListener(new OnItemClickHandler());
    }

    public void readLeaveApply(){
        JSONObject jsonObject = new JSONObject();
        url = "http://192.168.111.1:8080/getApply";
        sendReadLeaveApplyRequest(jsonObject);
    }

    public void sendReadLeaveApplyRequest(JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());
                try{
                    JSONArray detail = jsonObject.getJSONArray("detail");
                    JSONArray usernames = jsonObject.getJSONArray("usernames");

                    updateApplyList(detail, usernames);

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

    public void sendUpdateApplyRequest(JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());
                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                try{
                    JSONArray detail = jsonObject.getJSONArray("detail");
                    JSONArray usernames = jsonObject.getJSONArray("usernames");

                    results.clear();
                    updateApplyList(detail, usernames);
                    onResume(); //刷新

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
