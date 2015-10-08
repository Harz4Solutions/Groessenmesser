package com.harz4solutions.groessenmesser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Camera mCamera;
    private CameraView mPreview;
    private TextView alphaText;
    private TextView betaText;

    private Button saveTopB;
    private Button saveBottomB;

    private double alpha;
    private double alphaBeta;
    private double beta;

    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private double sensorAngle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> orientationSensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        orientationSensor = orientationSensors.get(0);

        if (checkCameraHardware(getApplicationContext())) { //Check if Device has a Camera
            // Create an instance of Camera
            mCamera = getCameraInstance();

            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraView(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraFrameLayout);
            preview.addView(mPreview, 0);

            alphaText = (TextView) findViewById(R.id.alpha);
            alphaText.setText("\u03B1 : _");
            betaText = (TextView) findViewById(R.id.beta);
            betaText.setText("\u03B2 : _");


            saveTopB = (Button) findViewById(R.id.saveTopLine);
            saveTopB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alphaBeta = (sensorAngle > 0) ? sensorAngle : sensorAngle * -1;
                    ;
                    if (alpha != 0) {
                        beta = alphaBeta - alpha;
                        alphaText.setText("\u03B1 : " + (int) alpha);
                        betaText.setText("\u03B2 : " + (int) beta);
                        Button button = (Button) findViewById(R.id.calculate);
                        button.setEnabled(true);
                    }
                }
            });
            saveBottomB = (Button) findViewById(R.id.saveBottomLine);
            saveBottomB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alpha = (sensorAngle > 0) ? sensorAngle : sensorAngle * -1;
                    if (alphaBeta != 0) {
                        beta = alphaBeta - alpha;
                        alphaText.setText("\u03B1 : " + (int) alpha);
                        betaText.setText("\u03B2 : " + (int) beta);
                        Button button = (Button) findViewById(R.id.calculate);
                        button.setEnabled(true);
                    }
                }
            });

            Button calculate = (Button) findViewById(R.id.calculate);
            final Context context = this;
            calculate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CalculateActivity.class);
                    intent.putExtra("alpha", alpha);
                    intent.putExtra("beta", beta);
                    startActivity(intent);
                }
            });


        }

    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            c.setDisplayOrientation(90);
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Toast.makeText(getApplicationContext(), "The Camera could not be accessed", Toast.LENGTH_SHORT).show();
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorAngle = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (mCamera == null) {
            mCamera = getCameraInstance();
            mPreview = new CameraView(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraFrameLayout);
            preview.addView(mPreview, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mPreview.setVisibility(View.GONE);
            mPreview.getHolder().getSurface().release();
            mPreview = null;
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
