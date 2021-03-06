package org.jembi.nearFieldHealth.nfcUtils;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.widget.Toast;

import org.jembi.nearFieldHealth.JsonConverter;
import org.jembi.nearFieldHealth.models.Patient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NfcWriter {

    private static final String ERROR_DETECTED = "No NFC tag detected!";
    private static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    private static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    private Context context;
    private Tag myTag;

    public NfcWriter(Context context) {
        this.context = context;
    }

    public boolean tryWrite(Patient patient) {
        try {
            if(myTag ==null) {
                Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show();
                return false;
            } else {
                write(patient, myTag);
                Toast.makeText(context, WRITE_SUCCESS, Toast.LENGTH_LONG ).show();
                return true;
            }
        } catch (IOException | FormatException e) {
            Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG ).show();
            e.printStackTrace();
            return false;
        }
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
        byte[] textBytes  = JsonConverter.convertToJson(patient).getBytes();
        byte[] langBytes  = lang.getBytes("UTF-8");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);
    }

    //todo not a great solution
    public void setTag(Tag myTag) {
        this.myTag = myTag;
    }
}
