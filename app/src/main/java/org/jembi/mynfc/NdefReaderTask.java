package org.jembi.mynfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.lang3.SerializationUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by barry on 2017/03/23.
 */

public class NdefReaderTask extends AsyncTask<Tag, Void, String> {

    public static final String TAG = "NfcDemo";

    TextView nfcOutput;

    public NdefReaderTask(TextView output){
        nfcOutput = output;
    }

    @Override
    protected String doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Unsupported Encoding", e);
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();
        //todo extract payload logic


        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"
        int payloadLength = payload.length - languageCodeLength -1;
        byte[] myBytes = new byte[payloadLength];
        System.arraycopy(payload, languageCodeLength+1, myBytes, 0, payloadLength);

        Patient patient = SerializationUtils.deserialize(myBytes);

        // Get the Text
        //return new String(payload, languageCodeLength + 1, payloadLength, textEncoding);
        return patient.Name;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            //todo a callback of some sort should go here
            nfcOutput.setText("Read content: " + result);
        }
    }
}