package org.jembi.mynfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {
    public static final String MIME_TEXT_PLAIN = "text/plain";

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";

    private TextView nfcOutput;
    private NfcAdapter nfcAdapter;
    Button btnWrite;
    Context context;
    TextView message;
    Tag myTag;
    NfcReader nfcReader;

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
                try {
                    if(myTag ==null) {
                        Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                    } else {
                        //write(message.getText().toString(), myTag);
                        write(GetPatient(), myTag);
                        Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG ).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                } catch (FormatException e) {
                    Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
                    e.printStackTrace();
                }
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

    private void write(Patient patient, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(patient) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    private NdefRecord createRecord(Patient patient) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = SerializationUtils.serialize(patient);
        byte[] langBytes  = lang.getBytes("UTF-8");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
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
        //if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //}
        nfcReader.handleIntent(intent);
    }
}
