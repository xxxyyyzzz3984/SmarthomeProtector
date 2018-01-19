package com.yinhaoxiao.testsensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    double ax, ay, az, light;   // these are the acceleration in x,y and z axis
    private List<String> mAccX, mAccY, mAccZ, mLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mAccX = new ArrayList<>();
        mAccY = new ArrayList<>();
        mAccZ = new ArrayList<>();
        mLight = new ArrayList<>();

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);

        Button PressBtn = (Button) findViewById(R.id.pressbtn);
        PressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccX.add("pressed");
                mAccY.add("pressed");
                mAccZ.add("pressed");
                mLight.add("pressed");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAccX.add(ax + "");
                    mAccY.add(ay + "");
                    mAccZ.add(az + "");
                    mLight.add(light + "");
                }
            }
        }).start();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("dataX: " + mAccX.toString());
                    System.out.println("dataY: " + mAccY.toString());
                    System.out.println("dataZ: " + mAccZ.toString());
                    System.out.println("dataL: " + mLight.toString());

                    mAccX.clear();
                    mAccY.clear();
                    mAccZ.clear();
                    mLight.clear();
                }

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = event.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
}
