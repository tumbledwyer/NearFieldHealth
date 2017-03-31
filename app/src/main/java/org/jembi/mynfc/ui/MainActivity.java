package org.jembi.mynfc.ui;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jembi.mynfc.JsonConverter;
import org.jembi.mynfc.models.HealthCareUser;
import org.jembi.mynfc.models.Immunisation;
import org.jembi.mynfc.nfcUtils.NfcReadEvent;
import org.jembi.mynfc.nfcUtils.NfcReader;
import org.jembi.mynfc.nfcUtils.NfcToken;
import org.jembi.mynfc.nfcUtils.NfcWriter;
import org.jembi.mynfc.R;
import org.jembi.mynfc.models.Patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NfcReadEvent {

    private TextView nfcOutput;
    private NfcAdapter nfcAdapter;
    Button btnWrite;
    Context context;
    TextView message;
    Tag myTag;
    NfcReader nfcReader;
    NfcWriter nfcWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcOutput = (TextView) findViewById(R.id.textView_explanation);
        btnWrite = (Button) findViewById(R.id.writeNfc);
        message = (TextView) findViewById(R.id.inputBox);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        context = this;

        nfcReader = new NfcReader(new NfcToken(this));
        nfcWriter = new NfcWriter(context);


        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            nfcOutput.setText("NFC is disabled.");
        } else {
            nfcOutput.setText("Waiting for NFC chip...");
        }

        btnWrite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                nfcWriter.tryWrite(GetPatient());
            }
        });

        nfcReader.handleIntent(getIntent());
    }

    private Patient GetPatient(){
        Patient patient = new Patient();
        patient.Id = 123;
        patient.Name = message.getText().toString();
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
        nfcReader.setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        nfcReader.stopForegroundDispatch(this, nfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) return;

        myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcWriter.setTag(myTag);
        nfcReader.handleIntent(intent);
    }

    @Override
    public void onReadComplete(String data) {
        HealthCareUser healthCareUser = JsonConverter.convertToHealthCareUser(data);
        if(healthCareUser.Role.equals("Nurse")){
            nfcOutput.setText("Hello " + healthCareUser.Name);
            Intent intent = new Intent(this, HcwPortalActivity.class);
            intent.putExtra("HCW", healthCareUser);
            startActivity(intent);
        } else {
            nfcOutput.setText("Nort bru");
        }
    }
}
