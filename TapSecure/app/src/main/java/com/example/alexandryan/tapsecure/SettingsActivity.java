package com.example.alexandryan.tapsecure;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    Spinner accountSpinner;
    EditText tapDollarDisplay;
    Switch tapSecureEnabledSwitch;
    ScrollView tapSecureSettingsHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tapSecureSettingsHolder = (ScrollView) findViewById(R.id.tapSecureSettingsHolder);
        tapSecureSettingsHolder.setVisibility(View.INVISIBLE);
        tapSecureEnabledSwitch = (Switch) findViewById(R.id.tapSecureEnabledSwitch);

        tapSecureEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    tapSecureSettingsHolder.setVisibility(View.VISIBLE);
                else
                    tapSecureSettingsHolder.setVisibility(View.INVISIBLE);
            }
        });

        setTitle("TD TapSecure Settings");
        tapDollarDisplay = (EditText) findViewById(R.id.amountEntered);
        tapDollarDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("text changed to " + tapDollarDisplay.getText());
                Float myF = Float.parseFloat(tapDollarDisplay.getText().toString());
                BankService.DebitCard.setTapLimit(myF);
            }
        });

        createSpinner();
        updateTapDollarDisplayFromSharedPrefs();

    }

    public void updateTapDollarDisplayFromSharedPrefs(){
        String cardText = accountSpinner.getSelectedItem().toString();
        if(cardText.equals("TD VISA - 2355336522565984")){
            Float f = BankService.getSharedPrefs().getFloat("VTapLimit",100f);
            tapDollarDisplay.setText(f.toString());
        } else if (cardText.equals("EVERY DAY CHEQUING - 3365864")){
            Float f = BankService.getSharedPrefs().getFloat("VTapLimit",100f);
            tapDollarDisplay.setText(f.toString());
        }
    }

    public void makeToast(){
        Toast.makeText(this, "done pressed", Toast.LENGTH_SHORT).show();
    }

    public void createSpinner(){
        List<String> accounts = new ArrayList<String>();
        if(BankService.getSharedPrefs().getBoolean("VInteracFlashEnabled", false)){
            accounts.add(BankService.VisaCard.getCardDescription());
        }
        if (BankService.getSharedPrefs().getBoolean("DInteracFlashEnabled", false)){
            accounts.add(BankService.DebitCard.getCardDescription());
        }

        // Creating adapter for spinner
        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, accounts);

        // attaching data adapter to spinner
        accountSpinner.setAdapter(adapter);
        // Drop down layout style - list view with radio button
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateTapDollarDisplayFromSharedPrefs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //NOTE: back button page routing is declared in manifest as the parent
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onDollarAmtClick(View view) {
        switch (view.getId()){
            case R.id.button50:
                tapDollarDisplay.setText("50");
                saveTapLimitInSharedPrefs(50f);
                break;
            case R.id.button100:
                tapDollarDisplay.setText("100");
                saveTapLimitInSharedPrefs(100f);
                break;
            case R.id.button250:
                tapDollarDisplay.setText("250");
                saveTapLimitInSharedPrefs(250f);
                break;
        }
    }

    public void saveTapLimitInSharedPrefs(float amt){
        if(BankService.getSharedPrefs().getBoolean("VInteracFlashEnabled", false)){ //if visa
            BankService.VisaCard.setTapLimit(amt);
        }
        if (BankService.getSharedPrefs().getBoolean("DInteracFlashEnabled", false)){ //if debit
            BankService.DebitCard.setTapLimit(amt);
        }
    }
}
