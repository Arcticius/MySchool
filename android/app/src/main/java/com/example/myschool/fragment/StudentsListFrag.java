package com.example.myschool.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myschool.R;
import com.example.myschool.activity.AddActivity;
import com.example.myschool.activity.DetailUpdaterActivity;
import com.example.myschool.entity.StudentListView;
import com.example.myschool.entity.User;
import com.example.myschool.util.EditTextWithClear;
import com.example.myschool.util.StuListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentsListFrag extends Fragment implements AdapterView.OnItemSelectedListener {

    //private String[] country = {"India", "USA", "China", "Japan", "Other"};
    private ArrayList<String> batches = new ArrayList<>();
    private ListView result;
    private ArrayList<StudentListView> results;
    private List<User> students = new ArrayList<>();
    private FloatingActionButton addButton, updateButton, normalButton;
    private boolean isAllFabsVisible;
    private TextView addText, updateText;
    private Spinner spinner;

    private SharedPreferences sharedPreferences;

    private Button cancelButton, submitButton;
    private EditTextWithClear uidText;
    private String uid;

    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_students_list, container, false);

        spinner = rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        result = (ListView)rootView.findViewById(R.id.students_list);
        result.setEmptyView(rootView.findViewById(R.id.text_empty));

        //初始化spinner，读取班级名
        initSpinner();

        results = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        students = (List<User>) intent.getSerializableExtra("students");

        //初始化：默认为全部学生
        // User 转 StudentListView
        for(int i = 0; i < students.size(); i++){
            StudentListView student = new StudentListView();
            student.setId(students.get(i).getUid());
            student.setName(students.get(i).getUsername());
            student.setLeave("Leave Available: " + students.get(i).getLeaveBalance().toString());
            results.add(student);
        }

        result.setAdapter(new StuListAdapter(rootView.getContext(), results));

        normalButton = rootView.findViewById(R.id.fab);
        addButton = rootView.findViewById(R.id.fab_add);
        updateButton = rootView.findViewById(R.id.fab_update);
        addText = (TextView) rootView.findViewById(R.id.text_add);
        updateText = (TextView)rootView.findViewById(R.id.text_update);

        isAllFabsVisible = false;

        result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView uidText = (TextView)view.findViewById(R.id.text_id);
                String uid = uidText.getText().toString();

                Intent intent = getActivity().getIntent();
                students = (List<User>) intent.getSerializableExtra("students");
                for(int i = 0; i < students.size(); i++){
                    User student = students.get(i);
                    if(student.getUid().equals(uid)){
                        updateStudentDetail(student);   //更新学生信息
                        break;
                    }
                }
            }
        });

        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllFabsVisible){
                    addButton.show();
                    updateButton.show();
                    addText.setVisibility(View.VISIBLE);
                    updateText.setVisibility(View.VISIBLE);

                    isAllFabsVisible = true;
                }
                else{
                    addButton.hide();
                    updateButton.hide();
                    addText.setVisibility(View.GONE);
                    updateText.setVisibility(View.GONE);

                    isAllFabsVisible = false;
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showUpdateDialog();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String batch = spinner.getSelectedItem().toString();
                updateList(batch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        return rootView;
    }

    public void showUpdateDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Update");
        dialog.setContentView(R.layout.update_dialog);

        cancelButton = (Button)dialog.findViewById(R.id.button_cancel_update);
        submitButton = (Button)dialog.findViewById(R.id.button_update_submit);
        uidText = (EditTextWithClear) dialog.findViewById(R.id.text_update_uid);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uid = uidText.getText().toString();
                boolean flag = false;
                int position = 0;

                //判断输入的uid是否存在
                for(int i = 0; i < students.size(); i++){
                    User student = students.get(i);
                    if(uid.equals(student.getUid())){
                        flag = true;
                        position = i;
                        break;
                    }
                }
                if(flag) //uid存在，转入更新页面
                    updateStudentDetail(students.get(position));
                else    //uid不存在
                    Toast.makeText(getContext(), "Invalid UID!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    //更新学生信息
    public void updateStudentDetail(User student){
        Intent intent = new Intent(getView().getContext(), DetailUpdaterActivity.class);
        intent.putExtra("uid", student.getUid());

        sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", student.getUid());
        editor.putString("username", student.getUsername());
        editor.putString("batch", student.getBatch());
        editor.putString("dob", student.getDob());
        editor.putString("email", student.getEmail());
        editor.putString("bloodGroup", student.getBloodGroup());
        editor.putString("contactNumber", student.getContactNumber());
        editor.putString("address", student.getAddress());

        editor.commit();

        startActivity(intent);
    }

    public void initSpinner(){
        JSONObject jsonObject = new JSONObject();
        url = "http://192.168.111.1:8080/batches";
        sendGetBatchesRequest(jsonObject);
    }

    //获取班级名
    public void sendGetBatchesRequest(JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());

                try{
                    JSONArray batchArray = jsonObject.getJSONArray("detail");
                    batches = toList(batchArray);

                    ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, batches);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
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

    //JSONArray转ArrayList
    public ArrayList<String> toList(JSONArray batchArray){
        ArrayList<String> batches = new ArrayList<>();

        batches.add("all");

        try{
            for(int i = 0; i < batchArray.length(); i++){
                batches.add((String)batchArray.get(i));
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return batches;
    }

    public void updateList(String batch){

        System.out.println(batch);

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("batch", batch);
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        url = "http://192.168.111.1:8080/batch/student";
        sendUpdateListRequest(jsonObject);
    }

    public void sendUpdateListRequest(JSONObject myJsonObject){
        requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, myJsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("TAG", jsonObject.toString());
                try{
                    results.clear();

                    JSONArray students = jsonObject.getJSONArray("detail");
                    for(int i = 0; i < students.length(); i++){
                        StudentListView student = new StudentListView();
                        student.setId(students.getJSONObject(i).getString("uid"));
                        student.setName(students.getJSONObject(i).getString("username"));
                        student.setLeave("Leave Available: " + students.getJSONObject(i).getInt("leaveBalance"));
                        results.add(student);
                    }

                    result.setAdapter(new StuListAdapter(getContext(), results));
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
