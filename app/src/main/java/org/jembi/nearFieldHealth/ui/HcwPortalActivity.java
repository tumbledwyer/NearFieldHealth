package org.jembi.nearFieldHealth.ui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jembi.nearFieldHealth.FakeDataFactory;
import org.jembi.nearFieldHealth.FileSystem;
import org.jembi.nearFieldHealth.JsonConverter;
import org.jembi.nearFieldHealth.R;
import org.jembi.nearFieldHealth.models.HealthCareUser;
import org.jembi.nearFieldHealth.models.Patient;
import org.jembi.nearFieldHealth.nfcUtils.NfcReadEvent;
import org.jembi.nearFieldHealth.nfcUtils.NfcReader;
import org.jembi.nearFieldHealth.nfcUtils.NfcToken;

import java.io.FileNotFoundException;
import java.io.IOException;

public class HcwPortalActivity extends AppCompatActivity implements NfcReadEvent {

    NfcReader nfcReader;
    NfcAdapter nfcAdapter;
    private TextView scanPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcw_portal);

        nfcReader = new NfcReader(new NfcToken(this));
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = getIntent();
        HealthCareUser hcw = (HealthCareUser)intent.getSerializableExtra("HCW");
        TextView welcome = (TextView) findViewById(R.id.welcomeText);
        scanPatient = (TextView) findViewById(R.id.scanPatient);
        welcome.setText("Hello " + hcw.Name);

        setupFakes();

        //nfcReader.handleIntent(getIntent());
    }

    private void setupFakes(){
        FloatingActionButton fakeScan = (FloatingActionButton) findViewById(R.id.fakeScan);
        fakeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Patient patient = FakeDataFactory.getPatient();
                patient.Role = "Patient";
                String jatient = JsonConverter.convertToJson(patient);
                onReadComplete(jatient);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent){
        nfcReader.handleIntent(intent);
    }

    @Override
    public void onReadComplete(String data)  {
        HealthCareUser healthCareUser = JsonConverter.convertToHealthCareUser(data);
        if(healthCareUser.Role.equals("Patient")){
            Patient pat = JsonConverter.convertToPatient(data);
            Patient patientFile = null;
            try {
                patientFile = FileSystem.read(this, pat);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, PatientViewActivity.class);
            intent.putExtra("Patient", patientFile);
            startActivity(intent);
        } else {
            scanPatient.setText("Not a patient");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //nfcReader.setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        //nfcReader.stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }
}
