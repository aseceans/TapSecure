package com.example.alexandryan.tapsecure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class SettingsActivity extends AppCompatActivity {
    Spinner accountSpinner;
    EditText tapDollarDisplay;
    ScrollView tapSecureSettingsHolder;
    boolean visaSelected;
    Switch tapSecureEnabledSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("TD TapSecure Settings");

        //initialize variables
        tapSecureSettingsHolder = (ScrollView) findViewById(R.id.tapSecureSettingsHolder);
        tapSecureEnabledSwitch = (Switch) findViewById(R.id.tapSecureEnabledSwitch);
        tapDollarDisplay = (EditText) findViewById(R.id.amountEntered);

        createSpinner();
        setVisaSelectedFlag();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //NOTE: back button page routing is declared in manifest as the parent

        addListeners();
    }

    public void setVisaSelectedFlag(){
        String spinnerText = accountSpinner.getSelectedItem().toString();
        if (spinnerText.equals("TD VISA - 2355336522565984")){
            visaSelected = true;
            Toast.makeText(this, "visa Selected", Toast.LENGTH_LONG).show();
        } else if (spinnerText.equals("EVERY DAY CHEQUING - 3365864")){
            visaSelected = false;
            Toast.makeText(this, "cheqing Selected", Toast.LENGTH_LONG).show();
        }

        if (visaSelected) {
            tapSecureEnabledSwitch.setChecked(BankService.VisaCard.getTapSecureEnabled());
            showHideViewBasedOnFlag(BankService.VisaCard.TapSecureEnabled, tapSecureSettingsHolder);
        }
        else {
            tapSecureEnabledSwitch.setChecked(BankService.DebitCard.getTapSecureEnabled());
            showHideViewBasedOnFlag(BankService.DebitCard.TapSecureEnabled, tapSecureSettingsHolder);
        }
    }
    public void createSpinner(){
        List<String> accounts = new ArrayList<String>();
        if(BankService.VisaCard.getInteracFlashEnabled()){
            accounts.add(BankService.VisaCard.getCardDescription());
        }
        if(BankService.DebitCard.getInteracFlashEnabled()) {
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
                setVisaSelectedFlag();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }
    public void addListeners(){
        tapSecureEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //show or hide bottom half based on switch
                showHideViewBasedOnFlag(isChecked, tapSecureSettingsHolder);

                if (visaSelected) {
                    BankService.VisaCard.setTapSecureEnabled(isChecked); //saved in shared prefs
                    showHideViewBasedOnFlag(BankService.VisaCard.TapSecureEnabled, tapSecureSettingsHolder);
                    // this is where all the logic for the bottom switches needs to go (FOR INSTANTIATION)
                }
                else {
                    BankService.DebitCard.setTapSecureEnabled(isChecked);
                    showHideViewBasedOnFlag(BankService.DebitCard.TapSecureEnabled, tapSecureSettingsHolder);
                    // this is where all the logic for the bottom switches needs to go (FOR INSTANTIATION)
                }
            }
        });
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
    }

    //shows or hides bottom half based on flag
    public void showHideViewBasedOnFlag(boolean switchFlag, View visibilityView){
        if(switchFlag){ //on
            visibilityView.setVisibility(View.VISIBLE);
        } else { //off
            visibilityView.setVisibility(View.INVISIBLE);
        }
    }

    public void onDollarAmtClick(View view) {
        switch (view.getId()){
            case R.id.button50:
                tapDollarDisplay.setText("50");
              //  saveTapLimitInSharedPrefs(50f);
                break;
            case R.id.button100:
                tapDollarDisplay.setText("100");
             //   saveTapLimitInSharedPrefs(100f);
                break;
            case R.id.button250:
                tapDollarDisplay.setText("250");
             //   saveTapLimitInSharedPrefs(250f);
                break;
        }
    }
}
