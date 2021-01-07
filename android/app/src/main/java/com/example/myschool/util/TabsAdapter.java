package com.example.myschool.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myschool.fragment.LeaveApprovalFrag;
import com.example.myschool.fragment.StudentsListFrag;

public class TabsAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;

    public TabsAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                StudentsListFrag studentsList = new StudentsListFrag();
                return studentsList;
            case 1:
                LeaveApprovalFrag leaveApproval = new LeaveApprovalFrag();
                return leaveApproval;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
