package com.example.myschool.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myschool.R;
import com.example.myschool.entity.StudentListView;

import java.util.ArrayList;

public class StuListAdapter extends BaseAdapter {

    private ArrayList<StudentListView> listData;
    private LayoutInflater layoutInflater;

    public StuListAdapter(Context context, ArrayList<StudentListView> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.student_list, null);

            holder = new ViewHolder();
            holder.uId = (TextView)convertView.findViewById(R.id.text_id);
            holder.uName = (TextView)convertView.findViewById(R.id.text_name);
            holder.uBalLeave = (TextView)convertView.findViewById(R.id.text_leave);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();

        holder.uId.setText(listData.get(position).getId());
        holder.uName.setText(listData.get(position).getName());
        holder.uBalLeave.setText(listData.get(position).getLeave());

        return convertView;
    }

    static class ViewHolder{
        TextView uId;
        TextView uName;
        TextView uBalLeave;
    }
}
