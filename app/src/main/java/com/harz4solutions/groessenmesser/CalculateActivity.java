package com.harz4solutions.groessenmesser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Created by simon on 9/24/2015.
 */
public class CalculateActivity extends Activity {

    private double alpha;
    private double beta;
    private double a;
    private double b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        Intent intent = getIntent();
        alpha = intent.getDoubleExtra("alpha",-1);
        beta = intent.getDoubleExtra("beta",-1);

        if(alpha==-1 || beta == -1){
            noValuesFoundError();
        }

        EditText alphaTextfield = (EditText) findViewById(R.id.alphaEditText);
        EditText betaTextfield = (EditText) findViewById(R.id.betaEditText);
        final EditText aTextfield = (EditText) findViewById(R.id.aEditText);
        final EditText bTextfield = (EditText) findViewById(R.id.bEditText);

        alphaTextfield.setEnabled(false);
        betaTextfield.setEnabled(false);
        bTextfield.setEnabled(false);

        alphaTextfield.setText("" + alpha);
        betaTextfield.setText("" + beta);

        aTextfield.requestFocus();

        //Todo: Not working properly
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
                    bTextfield.setText(""+b);
                }catch (NumberFormatException e){
                    //ToDo: Some error handling
                }
            }
        });
    }
    public double calculateHeight(){
        double radiantAlpha = alpha*(Math.PI / 180);
        double radiantBeta = beta*(Math.PI / 180);
        double radiantGamma = (180-alpha-beta)*(Math.PI / 180);
        double c = a / Math.sin(radiantAlpha);
        System.out.println("c:" + c);
        double h1 = Math.sqrt(Math.pow(c, 2) - Math.pow(a, 2));
        double h2 = Math.sqrt(Math.pow(c / Math.cos((beta-(90-alpha))*(Math.PI / 180)),2) - Math.pow(c,2));

        System.out.println("b1:" + (h1 + h2));
        System.out.println("b2:" + (c / Math.sin(radiantGamma)) * Math.sin(radiantBeta));
        return (c / Math.sin(radiantGamma)) * Math.sin(radiantBeta);
    }
    public void noValuesFoundError(){

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
