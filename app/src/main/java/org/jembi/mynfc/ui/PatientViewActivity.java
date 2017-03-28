package org.jembi.mynfc.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.jembi.mynfc.R;
import org.jembi.mynfc.databinding.ActivityPatientViewBinding;
import org.jembi.mynfc.models.Patient;

public class PatientViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view);
        Intent intent = getIntent();
        Patient patient = (Patient)intent.getSerializableExtra("Patient");
        ActivityPatientViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_patient_view);
        binding.setPatient(patient);

    }
}
