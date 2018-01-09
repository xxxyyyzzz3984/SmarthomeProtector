package com.yinhaoxiao.smarthomeprotector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  PackageManager mPackageManager;
    private ListView mInstalledAppLV;
    private StableArrayAdapter mAppAdapter;
    public static String SelectedAppName = "";
    public static boolean RunningState = true;
    public static Intent MonitorServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPackageManager = getPackageManager();
        List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        mInstalledAppLV = (ListView) findViewById(R.id.installedapp_listview);
        ArrayList<String> appList = new ArrayList<>();

        // get all installed apps and store them in a string array
        for (ApplicationInfo packageInfo : packages) {
            String SinglePackageName = packageInfo.packageName;
            if(SinglePackageName.contains("com.android")) {
                // system app, ignore
                continue;
            }
            appList.add(SinglePackageName);
        }
        mAppAdapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, appList);
        mInstalledAppLV.setAdapter(mAppAdapter);

        mInstalledAppLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MonitorServiceIntent == null) {
                    MonitorServiceIntent = new Intent(MainActivity.this, MonitorService.class);
                    SelectedAppName = mAppAdapter.getItem(position);
                    startActivity(new Intent(MainActivity.this, StateActivity.class));
                    startService(MonitorServiceIntent);
                }
            }
        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}


