package com.kovaciny.voicesecretary;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alarm_list);

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> item1 = new HashMap<String, String>();
        item1.put("name", "Baby Sissy");
        item1.put("alias", "monica");
        data.add(item1);
        Map<String, String> item2 = new HashMap<String, String>();
        item2.put("name", "Baby Poopy");
        item2.put("alias", "why");
        data.add(item2);

        String[] from = new String[]{"name", "alias"};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleAdapter sa = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, from, to);

//        ListView lv = (ListView) findViewById(R.id.listAlarms);
//        lv.setAdapter(sa);
        setListAdapter(sa);
    }

}
