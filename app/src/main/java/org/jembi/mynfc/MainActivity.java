package org.jembi.mynfc;

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


public class MainActivity extends AppCompatActivity {

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

        nfcReader = new NfcReader(nfcOutput);
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
            nfcOutput.setText("pew pew");
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
        patient.Name = message.getText().toString();
        patient.Married = false;
        patient.Age = 69;
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
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcWriter.setTag(myTag);
        }
        nfcReader.handleIntent(intent);
    }
}
