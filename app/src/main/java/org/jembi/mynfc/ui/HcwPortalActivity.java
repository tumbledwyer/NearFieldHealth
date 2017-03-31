package org.jembi.mynfc.ui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.jembi.mynfc.FileSystem;
import org.jembi.mynfc.JsonConverter;
import org.jembi.mynfc.R;
import org.jembi.mynfc.models.HealthCareUser;
import org.jembi.mynfc.models.Patient;
import org.jembi.mynfc.nfcUtils.NfcReadEvent;
import org.jembi.mynfc.nfcUtils.NfcReader;
import org.jembi.mynfc.nfcUtils.NfcToken;

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

        nfcReader.handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent){
        nfcReader.handleIntent(intent);
    }

    @Override
    public void onReadComplete(String data) throws IOException {
        HealthCareUser healthCareUser = JsonConverter.convertToHealthCareUser(data);
        if(healthCareUser.Role.equals("Patient")){
            Patient pat = JsonConverter.convertToPatient(data);
            Patient patientFile = FileSystem.read(this, pat);
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
        nfcReader.setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        nfcReader.stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }
}
