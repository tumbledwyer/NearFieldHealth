package org.jembi.nearFieldHealth.ui;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jembi.nearFieldHealth.JsonConverter;
import org.jembi.nearFieldHealth.models.HealthCareUser;
import org.jembi.nearFieldHealth.nfcUtils.NfcReadEvent;
import org.jembi.nearFieldHealth.nfcUtils.NfcReader;
import org.jembi.nearFieldHealth.nfcUtils.NfcToken;
import org.jembi.nearFieldHealth.nfcUtils.NfcWriter;
import org.jembi.nearFieldHealth.R;
import org.jembi.nearFieldHealth.models.Patient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NfcReadEvent {

    private TextView loginText;
    private NfcAdapter nfcAdapter;
    Tag myTag;

    Context context;

    FloatingActionButton btnWrite;

    NfcReader nfcReader;
    NfcWriter nfcWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginText = (TextView) findViewById(R.id.textView_explanation);
        btnWrite = (FloatingActionButton) findViewById(R.id.writeNfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        context = this;

        nfcReader = new NfcReader(new NfcToken(this));
        nfcWriter = new NfcWriter(context);


        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            //finish();
            //return;
        } else {

            if (!nfcAdapter.isEnabled()) {
                loginText.setText("NFC is disabled.");
            } else {
                loginText.setText("Waiting for NFC chip...");
            }
        }

        // Just for dev
        btnWrite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                nfcWriter.tryWrite(GetPatient());
            }
        });

        // nfcReader.handleIntent(getIntent());
    }

    private Patient GetPatient(){
        Patient patient = new Patient();
        patient.Id = 123;
        patient.Name = "Sally" + Math.floor(Math.random() * 1000);
        patient.Married = false;
        patient.Age = 69;
        patient.Role = "Patient";
        patient.Immunisations = new ArrayList<>();


        return patient;
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        //nfcReader.setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        //nfcReader.stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) return;

        myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcWriter.setTag(myTag);
        // nfcReader.handleIntent(intent);
    }

    @Override
    public void onReadComplete(String data) {
        HealthCareUser healthCareUser = JsonConverter.convertToHealthCareUser(data);
        if(healthCareUser.Role.equals("Nurse")){
            loginText.setText("Hello " + healthCareUser.Name);
            Intent intent = new Intent(this, HcwPortalActivity.class);
            intent.putExtra("HCW", healthCareUser);
            startActivity(intent);
        } else {
            loginText.setText("Nort bru");
        }
    }
}
