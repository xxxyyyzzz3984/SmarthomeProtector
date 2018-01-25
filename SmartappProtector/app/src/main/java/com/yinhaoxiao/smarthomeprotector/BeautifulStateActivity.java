package com.yinhaoxiao.smarthomeprotector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.skyfishjy.library.RippleBackground;

public class BeautifulStateActivity extends Activity {

    private Intent mMonitorServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_beautiful_state);

        mMonitorServiceIntent = new Intent(this, MonitorService.class);
        startService(mMonitorServiceIntent);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        ImageView imageView=(ImageView)findViewById(R.id.guardImg);
        if (MainActivity.RunningState) {
            rippleBackground.startRippleAnimation();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start anime
                if (MainActivity.RunningState) {
                    rippleBackground.stopRippleAnimation();
                    MainActivity.RunningState = false;
                    stopService(mMonitorServiceIntent);
                    try {
                        MonitorService.HttpNotifiRunner.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // end anime
                else  {
                    rippleBackground.startRippleAnimation();
                    MainActivity.RunningState = true;
                    startService(mMonitorServiceIntent);
                }
            }
        });

    }
}
