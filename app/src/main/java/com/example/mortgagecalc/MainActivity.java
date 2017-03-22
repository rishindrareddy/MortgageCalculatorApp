package com.example.mortgagecalc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String HOUSE_PRICE = "HOUSE_PRICE";
    public static final String DOWN_PAYMENT_AMOUNT = "DOWN_PAYMENT_AMOUNT";
    public static final String ANNUAL_INTEREST_RATE = "ANNUAL_INTEREST_RATE";
    public static final String MORTGAGE_LOAN_LENGTH = "MORTGAGE_LOAN_LENGTH";

    public boolean editFlag = false;
    public int rowid;

    public double housePrice;
    public double downPaymentAmount;
    public double annualInterestRate;
    public int lengthOfMortgageLoan;

    private EditText housePriceEditText;
    private EditText downPaymentAmountEditText;
    private EditText annualInterestRateEditText;

    private Spinner lengthOfMortgageLoanSpinner;
    private EditText monthlyPaymentEditText;
    private EditText totalPaymentEditText;

    public Spinner mType;
    public EditText mStr;
    public EditText mCity;
    public Spinner mState;
    public EditText mZip;
    public double mAmount;

    ArrayAdapter<CharSequence> adapter1;
    ArrayAdapter<CharSequence> adapter2;
    ArrayAdapter<CharSequence> adapter3;
    protected DrawerLayout drawer;

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
                resetForm();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Spinner spinner1 = (Spinner) findViewById(R.id.propertyTypeSpinner);
        adapter1 = ArrayAdapter.createFromResource(this, R.array.pType_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = (Spinner) findViewById(R.id.stateSpinner);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.state_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        Spinner spinner3 = (Spinner) findViewById(R.id.lengthOfMortgageLoanSpinner);
        adapter3 = ArrayAdapter.createFromResource(this, R.array.lengthOfMortgageLoan_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        if (savedInstanceState == null) {
            housePrice = 0.0;
            downPaymentAmount = 0.0;
            annualInterestRate = 0.0;
            lengthOfMortgageLoan = 0;
        } else {
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

        housePriceEditText = (EditText) findViewById(R.id.housePriceEditText);
        downPaymentAmountEditText = (EditText) findViewById(R.id.downPaymentAmountEditText);
        annualInterestRateEditText = (EditText) findViewById(R.id.annualInterestRateEditText);
        lengthOfMortgageLoanSpinner = (Spinner) findViewById(R.id.lengthOfMortgageLoanSpinner);
        monthlyPaymentEditText = (EditText) findViewById(R.id.monthlyPaymentEditText);

        final Button calculateButton = (Button) findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = verifyAndSave();
                if (status) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Calculation saved successfully!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            if (extras.containsKey("rowid")) {

                int a = extras.getInt("rowid");
                setform(a);
            }
        }
    }

    public boolean setform(int a) {

        this.editFlag = true;
        this.rowid = a;
        DatabaseHelper helper = new DatabaseHelper(this);
        Cursor result = helper.sendRowEntry(a);
        result.moveToFirst();

        //setting form with pre-filled values
        String type = result.getString(1);
        if (!type.equals(null)) {
            int typePosition = adapter1.getPosition(type);
            mType.setSelection(typePosition);
        }
        mStr.setText(result.getString(2));
        mCity.setText(result.getString(3));

        String state = result.getString(4);
        if (!state.equals(null)) {
            int statePosition = adapter2.getPosition(state);
            mState.setSelection(statePosition);
        }
        mZip.setText(result.getString(5));

        housePriceEditText.setText(result.getString(6));
        downPaymentAmountEditText.setText(result.getString(7));
        annualInterestRateEditText.setText(result.getString(9));

        String years = result.getString(10);
        if (!years.equals(null)) {
            int yearsPosition = adapter3.getPosition(years);
            lengthOfMortgageLoanSpinner.setSelection(yearsPosition);
        }

        monthlyPaymentEditText.setText(result.getString(11));
        return true;
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

        switch (id) {
            case R.id.nav_first_fragment:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
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

    private void calculate() {

        double monthlyIntRate = 0.0, loanAmount = 0.0, monthlyPayment = 0.0, totalPayment = 0.0;
        int months = 0;

        housePrice = Double.parseDouble(housePriceEditText.getText().toString());
        downPaymentAmount = Double.parseDouble(downPaymentAmountEditText.getText().toString());
        annualInterestRate = Double.parseDouble(annualInterestRateEditText.getText().toString());
        lengthOfMortgageLoan = Integer.parseInt(lengthOfMortgageLoanSpinner.getSelectedItem().toString());

        if (housePrice != 0.0 && downPaymentAmount != 0.0 && annualInterestRate != 0.0 && lengthOfMortgageLoan != 0) {

            monthlyIntRate = annualInterestRate / (12 * 100);
            months = lengthOfMortgageLoan * 12;
            loanAmount = housePrice - downPaymentAmount;

            monthlyPayment = ((loanAmount * monthlyIntRate) / (1 - Math.pow(1 + monthlyIntRate, -months)));
            monthlyPaymentEditText.setText(String.format("%.02f", monthlyPayment));

            mAmount = monthlyPayment;

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.missingEntries);

            builder.setPositiveButton(R.string.OK, null);
            builder.setMessage(R.string.provideEntries);

            AlertDialog errorDialog = builder.create();
            errorDialog.show();
        }
    }

    public Double computeLoanAmount(String price, String dPayment) {

        Double loanAmount;
        loanAmount = Double.parseDouble(price) - Double.parseDouble(dPayment);
        return loanAmount;
    }


    public boolean verifyAndSave() {

        String[] values = new String[11];
        values[0] = mType.getSelectedItem().toString();
        values[1] = mStr.getText().toString();
        values[2] = mCity.getText().toString();
        values[3] = mState.getSelectedItem().toString();
        values[4] = mZip.getText().toString();
        values[5] = housePriceEditText.getText().toString();
        values[6] = downPaymentAmountEditText.getText().toString();
        values[7] = computeLoanAmount(values[5], values[6]).toString();
        values[8] = annualInterestRateEditText.getText().toString();
        values[9] = lengthOfMortgageLoanSpinner.getSelectedItem().toString();
        values[10] = Double.toString(mAmount);

        String strAddr = values[1] + " " + values[2] + " " + values[3] + " " + values[4];
        if (getLocationFromAddress(strAddr) == null) {
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

            return false;
        } else {
            calculate();

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            if (editFlag) {

                return databaseHelper.updateRow(this.rowid, values);

            }

            databaseHelper.insertData(values);
        }
        return true;

    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
    }

    public void resetForm() {

        mType.setSelection(0);
        mStr.setText("");
        mCity.setText("");
        mState.setSelection(0);
        mZip.setText("");
        housePriceEditText.setText("0.0");
        downPaymentAmountEditText.setText("0.0");
        annualInterestRateEditText.setText("0.0");
        lengthOfMortgageLoanSpinner.setSelection(0);
        monthlyPaymentEditText.setText("");

    }

}
