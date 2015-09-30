package com.harz4solutions.groessenmesser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
        aTextfield.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    try {
                        System.out.println(aTextfield.getText().toString());
                        a = Double.parseDouble(aTextfield.getText().toString());
                        b = calculateHeight();
                        bTextfield.setText(""+b);
                    }catch (NumberFormatException e){
                        //ToDo: Some error handling
                    }
                }
            }
        });


    }
    public double calculateHeight(){
        double c = a / Math.sin(alpha);
        double h1 = Math.sqrt(c) - Math.sqrt(a);
        double h2 = Math.sqrt(a / Math.cos(beta-(90-alpha))) - Math.sqrt(a);

        System.out.println(c+" "+h1+" "+h2);
        return h1+h2;
    }
    public void noValuesFoundError(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
