package org.jembi.mynfc.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import org.jembi.mynfc.ImmunisationAdapter;
import org.jembi.mynfc.R;
import org.jembi.mynfc.databinding.ActivityPatientViewBinding;
import org.jembi.mynfc.models.Patient;

public class PatientViewActivity extends AppCompatActivity {

    private String vaccine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view);
        Intent intent = getIntent();
        Patient patient = (Patient)intent.getSerializableExtra("Patient");
        ActivityPatientViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_view);
        binding.setPatient(patient);

        ImmunisationAdapter adapter = new ImmunisationAdapter(this, patient.Immunisations);
        ListView listView = (ListView) findViewById(R.id.immunisationListView);
        listView.setAdapter(adapter);

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
                        vaccine = getResources().getStringArray(R.array.immunisations)[which];
                        //do something with it

                    }
                });
        builder.show();
    }
}
