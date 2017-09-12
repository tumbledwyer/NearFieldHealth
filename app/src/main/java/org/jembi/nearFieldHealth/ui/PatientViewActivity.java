package org.jembi.nearFieldHealth.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import org.jembi.nearFieldHealth.FileSystem;
import org.jembi.nearFieldHealth.ImmunisationAdapter;
import org.jembi.nearFieldHealth.R;
import org.jembi.nearFieldHealth.databinding.ActivityPatientViewBinding;
import org.jembi.nearFieldHealth.models.Immunisation;
import org.jembi.nearFieldHealth.models.Patient;
import org.jembi.nearFieldHealth.nfcUtils.NfcReadEvent;
import org.jembi.nearFieldHealth.nfcUtils.NfcReader;
import org.jembi.nearFieldHealth.nfcUtils.NfcToken;
import org.jembi.nearFieldHealth.nfcUtils.NfcWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientViewActivity extends AppCompatActivity implements NfcReadEvent {

    private NfcReader nfcReader;
    private NfcWriter nfcWriter;
    private Tag myTag;
    private NfcAdapter nfcAdapter;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view);
        Intent intent = getIntent();
        patient = (Patient)intent.getSerializableExtra("Patient");
        ActivityPatientViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_view);
        binding.setPatient(patient);

        ImmunisationAdapter adapter = new ImmunisationAdapter(this, patient.Immunisations);
        ListView listView = (ListView) findViewById(R.id.immunisationListView);
        listView.setAdapter(adapter);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcReader = new NfcReader(new NfcToken(this));
        nfcWriter = new NfcWriter(this);

        FloatingActionButton addImmunisation = (FloatingActionButton)findViewById(R.id.addImmunisation);
        addImmunisation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showDialog();

            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("pew pew")
                .setItems(R.array.immunisations, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        String vaccine = getResources().getStringArray(R.array.immunisations)[which];
                        try {
                            writeTag(vaccine);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
        builder.show();
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
       // nfcReader.stopForegroundDispatch(this, nfcAdapter);
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

    }
    public void writeTag(String vaccine) throws IOException {
        Immunisation immunisation = new Immunisation();
        immunisation.Type = vaccine;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        immunisation.Date = simpleDateFormat.format(new Date());
        patient.Immunisations.add(immunisation);
        FileSystem.write(this, patient);
    }
}
