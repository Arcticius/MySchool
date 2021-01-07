package com.example.myschool.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.myschool.R;
import com.example.myschool.entity.LeaveListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class LeaveListAdapter extends BaseAdapter {

    private ArrayList<LeaveListView> listData;
    private LayoutInflater layoutInflater;
    private View.OnClickListener onApproveButtonClickListener, onRejectButtonClickListener;

    public LeaveListAdapter(Context context, ArrayList<LeaveListView> listData, View.OnClickListener onApproveButtonClickListener, View.OnClickListener onRejectButtonClickListener) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.onApproveButtonClickListener = onApproveButtonClickListener;
        this.onRejectButtonClickListener = onRejectButtonClickListener;
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
            convertView = layoutInflater.inflate(R.layout.leave_approval_list, null);

            holder = new ViewHolder();
            holder.uName = (TextView)convertView.findViewById(R.id.approval_name);
            holder.uUid = (TextView)convertView.findViewById(R.id.approval_uid);
            holder.uDate = (TextView)convertView.findViewById(R.id.approval_date);
            holder.uReason = (TextView)convertView.findViewById(R.id.approval_reason);
            holder.approveButton = (Button)convertView.findViewById(R.id.button_approval_approve);
            holder.rejectButton = (Button)convertView.findViewById(R.id.button_approval_reject);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();

        holder.uName.setText(listData.get(position).getName());
        holder.uUid.setText(listData.get(position).getUid());
        holder.uDate.setText(listData.get(position).getDate());
        holder.uReason.setText(listData.get(position).getReason());

        holder.approveButton.setOnClickListener(this.onApproveButtonClickListener);
        holder.rejectButton.setOnClickListener(this.onRejectButtonClickListener);

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("leaveId", listData.get(position).getId());
            jsonObject.put("uid", listData.get(position).getUid());
            holder.approveButton.setTag(jsonObject);
            holder.rejectButton.setTag(jsonObject);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder{
        TextView uName;
        TextView uUid;
        TextView uDate;
        TextView uReason;
        Button approveButton;
        Button rejectButton;
    }
}
