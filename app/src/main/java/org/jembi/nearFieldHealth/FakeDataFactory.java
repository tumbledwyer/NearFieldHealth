package org.jembi.nearFieldHealth;

import org.jembi.nearFieldHealth.models.Patient;

import java.util.ArrayList;

public class FakeDataFactory {
    public static Patient getPatient(){
        Patient patient = new Patient();
        patient.Id = Math.round(Math.random() * 1000);
        patient.Name = "Sally" + Math.round(Math.random() * 1000);
        patient.Age = 69;
        patient.Role = "Patient";
        patient.Immunisations = new ArrayList<>();
        return patient;
    }
}
