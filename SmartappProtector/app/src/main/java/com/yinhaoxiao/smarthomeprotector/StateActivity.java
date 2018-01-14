package com.yinhaoxiao.smarthomeprotector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class StateActivity extends AppCompatActivity {
    private Button mRunBtn;
    private TextView mRunTextView;
    private Intent mMonitorServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mMonitorServiceIntent = new Intent(this, MonitorService.class);
        startService(mMonitorServiceIntent);

        mRunTextView= (TextView) findViewById(R.id.runningstateTV);
        mRunBtn = (Button) findViewById(R.id.runBtn);
        mRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.RunningState) {
                    mRunBtn.setText("Run");
                    mRunTextView.setText("Inactive");
                    MainActivity.RunningState = false;
                    stopService(mMonitorServiceIntent);
                    try {
                        MonitorService.HttpNotifiRunner.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    mRunBtn.setText("Stop");
                    mRunTextView.setText("Running");
                    MainActivity.RunningState = true;
                    startService(mMonitorServiceIntent);
                }
            }
        });
    }

}
