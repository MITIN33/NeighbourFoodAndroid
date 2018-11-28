package com.start.neighbourfood.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.start.neighbourfood.R;

public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        String[] array = new String[]{"Want to cancel the order ?", "Want report this user ?", "Others"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item,array);
        ListView listView = findViewById(R.id.helpCenter_listView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Toast.makeText(HelpActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
