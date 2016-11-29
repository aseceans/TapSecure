package com.example.alexandryan.tapsecure;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Handler;
import android.os.Message;
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
    Switch cumulativeSwitch;
    Switch tapActiveSwitch;
    Switch notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("TD TapSecure Settings");
        //initialize variables
        tapSecureSettingsHolder = (ScrollView) findViewById(R.id.tapSecureSettingsHolder);
        tapSecureEnabledSwitch = (Switch) findViewById(R.id.tapSecureEnabledSwitch);
        tapDollarDisplay = (EditText) findViewById(R.id.amountEntered);
        cumulativeSwitch = (Switch) findViewById(R.id.cumulativeSwitch);
        tapActiveSwitch = (Switch) findViewById(R.id.activeSwitch);
        notificationsSwitch = (Switch) findViewById(R.id.NotificationsSwitch);
        createSpinner();
        setVisaSelectedFlag();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //NOTE: back button page routing is declared in manifest as the parent
        addListeners();
        pubnubService.currentActivity = this;
        NFCService.initNFC();
        try{
           String tapped = getIntent().getExtras().getString("name");
            handleTapInput(tapped);
        }catch(Exception ex)
        {

        }
    }

    private void handleTapInput(String s)
    {
        if(s.equals("Visa"))
        {
            BankService.VisaCard.setTapSecure1MinActive(true);
            accountSpinner.setSelection(0);
            setVisaSelectedFlag();
        }else if (s.equals("Debit"))
        {
            BankService.DebitCard.setTapSecure1MinActive(true);
            if(accountSpinner.getAdapter().getCount() == 1)
                accountSpinner.setSelection(0);
            else
                accountSpinner.setSelection(1);
            setVisaSelectedFlag();
        }
        Toast.makeText(this, s + " is activated!", Toast.LENGTH_SHORT).show();
    }

    private Handler tapHandle = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle data = msg.getData();
            String mydata = data.getString("message");
            handleTapInput(mydata);
        }
    };


    private Handler cumulativeTotalHandle = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle data = msg.getData();
            String myString = data.getString("type");
            if(cumulativeSwitch.isChecked()) {
                if (myString.equals("Visa") && visaSelected) {
                    cumulativeSwitch.setText("Cumulative Mode \n(Current Total: $" + BankService.VisaCard.getCumAmount() + ")");
                } else if (myString.equals("Debit") && !visaSelected) {
                    cumulativeSwitch.setText("Cumulative Mode \n(Current Total: $" + BankService.DebitCard.getCumAmount() + ")");
                }

            }
        }
    };

    private Handler tapGUItoggle = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle data = msg.getData();
            String mydata = data.getString("message");
            if(mydata.equals("Visa") && !visaSelected)
                BankService.VisaCard.setTapSecure1MinActive(false);
            else if(mydata.equals("Visa") && visaSelected)
                tapActiveSwitch.setChecked(false);
            else if (mydata.equals("Debit") && !visaSelected)
                tapActiveSwitch.setChecked(false);
            else if (mydata.equals("Debit") && visaSelected)
                BankService.DebitCard.setTapSecure1MinActive(false);
        }
    };

    public Handler getTapGUItoggle() {return tapGUItoggle;}
    public Handler getTapHandle() {return tapHandle;}
    public Handler getCumulativeTotalHandle() {return cumulativeTotalHandle;}

    public void setVisaSelectedFlag(){
        String spinnerText = accountSpinner.getSelectedItem().toString();
        if (spinnerText.equals(BankService.VisaCard.CardDescription)){
            visaSelected = true;
            //Toast.makeText(this, "visa Selected", Toast.LENGTH_LONG).show();
        } else if (spinnerText.equals(BankService.DebitCard.CardDescription)){
            visaSelected = false;
            //Toast.makeText(this, "chequing Selected", Toast.LENGTH_LONG).show();
        }

        if (visaSelected) {
            tapSecureEnabledSwitch.setChecked(BankService.VisaCard.getTapSecureEnabled());
            showHideViewBasedOnFlag(BankService.VisaCard.TapSecureEnabled, tapSecureSettingsHolder);
            updateLowerValues();
        }
        else {
            tapSecureEnabledSwitch.setChecked(BankService.DebitCard.getTapSecureEnabled());
            showHideViewBasedOnFlag(BankService.DebitCard.TapSecureEnabled, tapSecureSettingsHolder);
            updateLowerValues();
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
    public void addListeners() {
        tapSecureEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //show or hide bottom half based on switch
                showHideViewBasedOnFlag(isChecked, tapSecureSettingsHolder);

                if (visaSelected) {
                    BankService.VisaCard.setTapSecureEnabled(isChecked); //saved in shared prefs
                    showHideViewBasedOnFlag(BankService.VisaCard.TapSecureEnabled, tapSecureSettingsHolder);
                } else {
                    BankService.DebitCard.setTapSecureEnabled(isChecked);
                    showHideViewBasedOnFlag(BankService.DebitCard.TapSecureEnabled, tapSecureSettingsHolder);
                }
                updateLowerValues();
            }
        });

        cumulativeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (visaSelected) {
                    BankService.VisaCard.setCumModeEnabled(isChecked); //saved in shared prefs
                    if(BankService.VisaCard.getCumModeEnabled())
                        cumulativeSwitch.setText("Cumulative Mode \n(Current Total: $" + BankService.VisaCard.getCumAmount() + ")");
                    else
                        cumulativeSwitch.setText("Cumulative Mode");
                } else {
                    BankService.DebitCard.setCumModeEnabled(isChecked);
                    if(BankService.DebitCard.getCumModeEnabled())
                        cumulativeSwitch.setText("Cumulative Mode \n(Current Total: $" + BankService.VisaCard.getCumAmount() + ")");
                    else
                        cumulativeSwitch.setText("Cumulative Mode");
                }
            }

        });

        tapActiveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (visaSelected) {
                    BankService.VisaCard.setTapSecure1MinActive(isChecked); //saved in shared prefs
                } else {
                    BankService.DebitCard.setTapSecure1MinActive(isChecked);
                }
            }

        });

        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (visaSelected) {
                    BankService.VisaCard.setNotificationsEnabled(isChecked); //saved in shared prefs
                } else {
                    BankService.DebitCard.setNotificationsEnabled(isChecked);
                }
            }

        });



        tapDollarDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(tapDollarDisplay.getText().length() > 0) {
                    if (visaSelected) {
                        BankService.VisaCard.setTapLimit(Float.parseFloat(tapDollarDisplay.getText().toString()));
                    } else {
                        BankService.DebitCard.setTapLimit(Float.parseFloat(tapDollarDisplay.getText().toString()));
                    }
                }
            }
        });
    }

    public void updateLowerValues()
    {
        if(visaSelected) {
            tapDollarDisplay.setText(BankService.VisaCard.getTapLimit().toString());
            cumulativeSwitch.setChecked(BankService.VisaCard.getCumModeEnabled());
            tapActiveSwitch.setChecked(BankService.VisaCard.getTapSecure1MinActive());
            notificationsSwitch.setChecked(BankService.VisaCard.getNotificationsEnabled());
            if(BankService.VisaCard.getCumModeEnabled())
                cumulativeSwitch.setText("Cumulative Mode \n(Current Total: $" + BankService.VisaCard.getCumAmount() + ")");
            else
                cumulativeSwitch.setText("Cumulative Mode");
        }
        else {
            tapDollarDisplay.setText(BankService.DebitCard.getTapLimit().toString());
            cumulativeSwitch.setChecked(BankService.DebitCard.getCumModeEnabled());
            tapActiveSwitch.setChecked(BankService.DebitCard.getTapSecure1MinActive());
            notificationsSwitch.setChecked(BankService.DebitCard.getNotificationsEnabled());
            if(BankService.DebitCard.getCumModeEnabled())
                cumulativeSwitch.setText("Cumulative Mode \n(Current Total: $" + BankService.DebitCard.getCumAmount() + ")");
            else
                cumulativeSwitch.setText("Cumulative Mode");

        }
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
            case R.id.button50: {
                if(visaSelected) {
                    BankService.VisaCard.setTapLimit(50);
                    tapDollarDisplay.setText(BankService.VisaCard.getTapLimit().toString());
                }
                else {
                    BankService.DebitCard.setTapLimit(50);
                    tapDollarDisplay.setText(BankService.DebitCard.getTapLimit().toString());
                }
                break;
            }
            case R.id.button100: {
                if(visaSelected) {
                    BankService.VisaCard.setTapLimit(100);
                    tapDollarDisplay.setText(BankService.VisaCard.getTapLimit().toString());
                }
                else {
                    BankService.DebitCard.setTapLimit(100);
                    tapDollarDisplay.setText(BankService.DebitCard.getTapLimit().toString());
                }
                break;
            }
            case R.id.button250: {
                if(visaSelected) {
                    BankService.VisaCard.setTapLimit(250);
                    tapDollarDisplay.setText(BankService.VisaCard.getTapLimit().toString());
                }
                else {
                    BankService.DebitCard.setTapLimit(250);
                    tapDollarDisplay.setText(BankService.DebitCard.getTapLimit().toString());
                }
                break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        NFCService.NFConNewIntent(intent, this);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        NFCService.NFConResume(SettingsActivity.class, this);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        NFCService.NFConPause(this);
        super.onPause();
    }
}
