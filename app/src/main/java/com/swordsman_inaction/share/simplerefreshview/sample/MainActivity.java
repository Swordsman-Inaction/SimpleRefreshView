package com.swordsman_inaction.share.simplerefreshview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.swordsman_inaction.share.simplerefreshview.R;
import com.swordsman_inaction.share.simplerefreshview.SimpleRefreshAbsListView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] menu = new String[]{"ListView", "GridView"};

        ListView listView = (ListView)findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        openActivity(ListViewActivity.class);
                        break;
                    case 1:
                        openActivity(GridViewActivity.class);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void openActivity(Class<?> activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

}
