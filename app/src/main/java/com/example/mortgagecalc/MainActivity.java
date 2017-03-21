package com.example.mortgagecalc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String HOUSE_PRICE = "HOUSE_PRICE";
    public static final String DOWN_PAYMENT_AMOUNT = "DOWN_PAYMENT_AMOUNT";
    public static final String ANNUAL_INTEREST_RATE = "ANNUAL_INTEREST_RATE";
    public static final String MORTGAGE_LOAN_LENGTH = "MORTGAGE_LOAN_LENGTH";

    public double housePrice;
    public double downPaymentAmount;
    public double annualInterestRate;
    public int lengthOfMortgageLoan;

    private EditText housePriceEditText;
    private EditText downPaymentAmountEditText;
    private EditText annualInterestRateEditText;
    private EditText lengthOfMortgageLoanEditText;
    private EditText monthlyPaymentEditText;
    private EditText totalPaymentEditText;

    public Spinner mType;
    public EditText mStr;
    public EditText mCity;
    public Spinner mState;
    public EditText mZip;
    public double mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Spinner spinner1 = (Spinner) findViewById(R.id.propertyTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pType_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        Spinner spinner2 = (Spinner) findViewById(R.id.stateSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.state_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);

        if(savedInstanceState == null){
            housePrice = 0.0;
            downPaymentAmount = 0.0;
            annualInterestRate = 0.0;
            lengthOfMortgageLoan = 0;
        }
        else
        {
            housePrice = savedInstanceState.getDouble(HOUSE_PRICE);
            downPaymentAmount = savedInstanceState.getDouble(DOWN_PAYMENT_AMOUNT);
            annualInterestRate = savedInstanceState.getDouble(ANNUAL_INTEREST_RATE);
            lengthOfMortgageLoan = savedInstanceState.getInt(MORTGAGE_LOAN_LENGTH);

        }

        mType = (Spinner) findViewById(R.id.propertyTypeSpinner);
        mStr = (EditText) findViewById(R.id.streetAddressEditText);
        mCity = (EditText) findViewById(R.id.cityEditText);
        mState = (Spinner) findViewById(R.id.stateSpinner);
        mZip = (EditText) findViewById(R.id.zipEditText);

        housePriceEditText = (EditText)findViewById(R.id.housePriceEditText);
        downPaymentAmountEditText = (EditText)findViewById(R.id.downPaymentAmountEditText);
        annualInterestRateEditText = (EditText)findViewById(R.id.annualInterestRateEditText);
        lengthOfMortgageLoanEditText = (EditText)findViewById(R.id.lengthOfMortgageLoanEditText);
        monthlyPaymentEditText = (EditText)findViewById(R.id.monthlyPaymentEditText);

        final Button calculateButton = (Button)findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndSave();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_second_fragment:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void calculate(){

        double monthlyIntRate = 0.0, loanAmount = 0.0, monthlyPayment = 0.0, totalPayment = 0.0;
        int months = 0;

        housePrice = Double.parseDouble(housePriceEditText.getText().toString());
        downPaymentAmount = Double.parseDouble(downPaymentAmountEditText.getText().toString());
        annualInterestRate = Double.parseDouble(annualInterestRateEditText.getText().toString());
        lengthOfMortgageLoan = Integer.parseInt(lengthOfMortgageLoanEditText.getText().toString());

        if(housePrice != 0.0 && downPaymentAmount != 0.0 && annualInterestRate != 0.0 && lengthOfMortgageLoan !=0) {

            monthlyIntRate = annualInterestRate / (12 * 100);
            months = lengthOfMortgageLoan * 12;
            loanAmount = housePrice - downPaymentAmount;

            monthlyPayment = ((loanAmount * monthlyIntRate) / (1 - Math.pow(1 + monthlyIntRate, -months)));
            monthlyPaymentEditText.setText(String.format("%.02f", monthlyPayment));

            mAmount = monthlyPayment;

        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.missingEntries);

            builder.setPositiveButton(R.string.OK, null);
            builder.setMessage(R.string.provideEntries);

            AlertDialog errorDialog = builder.create();
            errorDialog.show();
        }
    }

    public void verifyAndSave(){

        String[] values = new String[10];
        values[0] = mType.getSelectedItem().toString();
        values[1] = mStr.getText().toString();
        values[2] = mCity.getText().toString();
        values[3] = mState.getSelectedItem().toString();
        values[4] = mZip.getText().toString();
        values[5] = housePriceEditText.getText().toString();
        values[6] = downPaymentAmountEditText.getText().toString();
        values[7] = annualInterestRateEditText.getText().toString();
        values[8] = lengthOfMortgageLoanEditText.getText().toString();
        values[9] = Double.toString(mAmount);

        String strAddr = values[1] + " "+ values[2]+ " "+ values[3]+ " "+ values[4];
        if( getLocationFromAddress(strAddr) == null){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Invalid Address!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else {

            DatabaseHelper myDB = new DatabaseHelper(this);
            myDB.insertData(values);

            LatLng p;
            //just to check
            Cursor res = myDB.getAllData();
            System.out.println("Res: "+ res);

            if (res.moveToFirst()){
                do{
                    String data = res.getString(2)+" " + res.getString(3)+ " "+ res.getString(4)+" "+ res.getString(5) + " ";
                    System.out.println("data entry: "+ data);
                    p = getLocationFromAddress(data);
                    System.out.println("LatLong: "+ p);
                }while(res.moveToNext());
            }
            res.close();

        }

    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            System.out.println("address: "+ address);
            if (address.size() == 0) {
                return null;
            }
            Address location= address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude() , location.getLongitude());
        }
        catch (IOException ex) {

           ex.printStackTrace();
        }
        return p1;
    }
}
