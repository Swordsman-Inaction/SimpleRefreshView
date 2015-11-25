package com.swordsman_inaction.share.simplerefreshview.sample.adapter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleAdapter extends BaseAdapter {

    private int mCount = 50;

    public SimpleAdapter(){
    }

    public SimpleAdapter(int count){
        mCount = count;
    }

    public void inCreaseCount() {
        mCount += 10;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            TextView view = new TextView(parent.getContext());
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, parent.getResources().getDisplayMetrics()));

            view.setLayoutParams(params);
            view.setTextColor(Color.BLACK);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            view.setGravity(Gravity.CENTER);
            convertView = view;
        }
        ((TextView)convertView).setText("" + position);
        return convertView;
    }
}
