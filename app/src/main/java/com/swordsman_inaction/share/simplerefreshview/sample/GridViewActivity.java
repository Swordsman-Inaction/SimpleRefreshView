package com.swordsman_inaction.share.simplerefreshview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.GridView;
import android.widget.Toast;

import com.swordsman_inaction.share.simplerefreshview.R;
import com.swordsman_inaction.share.simplerefreshview.SimpleRefreshAbsListView;
import com.swordsman_inaction.share.simplerefreshview.sample.adapter.SimpleAdapter;


public class GridViewActivity extends Activity {

    private Handler mHandler;

    private SimpleRefreshAbsListView simpleRefreshAbsListView;

    private SimpleAdapter mSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        mHandler = new Handler(getMainLooper());

        simpleRefreshAbsListView = (SimpleRefreshAbsListView) findViewById(R.id.simpleRefreshGridView);
        GridView gridView = (GridView) simpleRefreshAbsListView.getAbsListView();
        mSimpleAdapter = new SimpleAdapter(20);
        gridView.setAdapter(mSimpleAdapter);

        simpleRefreshAbsListView.setOnPullDownLoadListener(new SimpleRefreshAbsListView.OnPullDownLoadListener() {
            @Override
            public void onPullDownLoad() {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        simpleRefreshAbsListView.setPullDownLoadingFinished();
                    }
                }, 2500);
            }
        });

        simpleRefreshAbsListView.setOnPullUpLoadListener(new SimpleRefreshAbsListView.OnPullUpLoadListener() {
            @Override
            public void onPullUpLoad() {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSimpleAdapter.inCreaseCount();
                        simpleRefreshAbsListView.setPullUpLoadingFinished();
                    }
                }, 2500);
            }
        });

    }

}
