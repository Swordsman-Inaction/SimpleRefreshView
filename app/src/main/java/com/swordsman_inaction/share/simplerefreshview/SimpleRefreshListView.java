package com.swordsman_inaction.share.simplerefreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

public class SimpleRefreshListView extends SimpleRefreshAbsListView {
    public SimpleRefreshListView(Context context) {
        super(context);
    }

    public SimpleRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.simple_refresh_listview, this, true);
    }
}
