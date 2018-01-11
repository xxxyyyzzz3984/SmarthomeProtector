package com.yinhaoxiao.smarthomeprotector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertActivity extends AppCompatActivity {
    public static int UserDecision = -1; // allow = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

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
            }
        });

        Button DenyBtn = (Button) findViewById(R.id.DenyBtn);
        DenyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDecision = 2;
            }
        });
    }
}
