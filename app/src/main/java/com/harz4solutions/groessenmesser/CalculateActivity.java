package com.harz4solutions.groessenmesser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class CalculateActivity extends Activity {

    private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

    private double alpha;
    private double beta;
    private double a;
    private double b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        //Get passed values
        Intent intent = getIntent();
        alpha = intent.getDoubleExtra("alpha", -1);
        beta = intent.getDoubleExtra("beta", -1);

        if (alpha == -1 || beta == -1) {
            Toast.makeText(getApplicationContext(), "Uuups! An error occurred. Try again!", Toast.LENGTH_SHORT).show();

        }

        //Get text fields
        EditText alphaTextfield = (EditText) findViewById(R.id.alphaEditText);
        EditText betaTextfield = (EditText) findViewById(R.id.betaEditText);
        final EditText aTextfield = (EditText) findViewById(R.id.aEditText);
        final EditText bTextfield = (EditText) findViewById(R.id.bEditText);

        //disable fields
        alphaTextfield.setEnabled(false);
        betaTextfield.setEnabled(false);
        bTextfield.setEnabled(false);

        //set values to fields
        alphaTextfield.setText("" + alpha);
        betaTextfield.setText("" + beta);

        aTextfield.requestFocus();

        //calculate on change
        aTextfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    System.out.println(aTextfield.getText().toString());
                    a = Double.parseDouble(aTextfield.getText().toString());
                    b = calculateHeight();
                    bTextfield.setText("" + b);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * calculates the height b , requires distance a and angles a and b
     *
     * @return height b formatted with two decimal places
     */
    public double calculateHeight() {
        double radiantAlpha = alpha * (Math.PI / 180);
        double radiantBeta = beta * (Math.PI / 180);
        double radiantGamma = (180 - alpha - beta) * (Math.PI / 180);
        double c = a / Math.sin(radiantAlpha);
        System.out.println("c:" + c);
        double h1 = Math.sqrt(Math.pow(c, 2) - Math.pow(a, 2));
        double h2 = Math.sqrt(Math.pow(c / Math.cos((beta - (90 - alpha)) * (Math.PI / 180)), 2) - Math.pow(c, 2));

        System.out.println("b1:" + (h1 + h2));
        System.out.println("b2:" + (c / Math.sin(radiantGamma)) * Math.sin(radiantBeta));

        return formatDouble((c / Math.sin(radiantGamma)) * Math.sin(radiantBeta));
    }

    /**
     * formats a double to have two decimal places
     *
     * @param d Double to be formatted
     * @return formatted double
     */
    public double formatDouble(double d) {
        String formattedD = new DecimalFormat("#.00").format(d);
        return Double.parseDouble(formattedD);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu item to submit height
        MenuItem menuItem = menu.add("Submit");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (b != 0) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String logMsg = intent.getStringExtra("SCAN_RESULT");
                log(logMsg);
            }
        }
    }

    /**
     * Adds a string to the LOG
     *
     * @param qrCode string from the Qr code
     */
    private void log(String qrCode) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }
        //Creating a json object
        JSONObject json = new JSONObject();
        try {
            json.put("task", "Groessenmesser");
            json.put("object", qrCode);
            json.put("height", "" + b);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Values could not be saved", Toast.LENGTH_SHORT).show();

        }
        String logmessage = json.toString();
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
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
