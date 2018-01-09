package com.yinhaoxiao.smarthomeprotector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class StateActivity extends AppCompatActivity {
    private Button mRunBtn;
    private TextView mRunTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mRunTextView= (TextView) findViewById(R.id.runningstateTV);
        mRunBtn = (Button) findViewById(R.id.runBtn);
        mRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.RunningState) {
                    mRunBtn.setText("Run");
                    mRunTextView.setText("Inactive");
                    MainActivity.RunningState = false;
                    stopService(MainActivity.MonitorServiceIntent);
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
                    startService(MainActivity.MonitorServiceIntent);
                }
            }
        });
    }
}
