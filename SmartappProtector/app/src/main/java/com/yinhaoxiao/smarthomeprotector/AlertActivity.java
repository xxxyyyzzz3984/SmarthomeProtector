package com.yinhaoxiao.smarthomeprotector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class AlertActivity extends Activity {
    public static int UserDecision = -1; // allow = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alert);

        Shimmer shimmer = new Shimmer();
        shimmer.start((ShimmerTextView) findViewById(R.id.textView2));

        UserDecision = -1;
        TextView Addr_TextView = (TextView) findViewById(R.id.AlertActIPTV);
        TextView OPTextView = (TextView) findViewById(R.id.AlertOPTV);
        String Addr_Str = "Source IP: " + HttpHandler.AlertIP + "\n";
        Addr_Str += "Dest IP: " + HttpHandler.TargetIP + "\n";
        Addr_Str += "Source MAC:" + HttpHandler.AlertMAC + "\n";
        Addr_Str += "Dest MAC:" + HttpHandler.TargetMAC;
        String OP_Str = "Dangerous Operation: " + HttpHandler.DgOP;
        Addr_TextView.setText(Addr_Str);
        OPTextView.setText(OP_Str);

        Button AllowBtn = (Button) findViewById(R.id.AllowBtn);
        AllowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDecision = 1;
                AlertActivity.this.finish();
            }
        });

        Button DenyBtn = (Button) findViewById(R.id.DenyBtn);
        DenyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDecision = 2;
                AlertActivity.this.finish();

            }
        });
    }
}
