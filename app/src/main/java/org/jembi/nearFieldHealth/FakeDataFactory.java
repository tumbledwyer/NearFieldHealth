package org.jembi.nearFieldHealth;

import org.jembi.nearFieldHealth.models.Patient;

import java.util.ArrayList;

public class FakeDataFactory {
    public static Patient getPatient(){
        Patient patient = new Patient();
        patient.Id = Math.round(Math.random() * 1000);
        patient.Name = GetRandomName();
        patient.Age = (int)Math.round(Math.random() * 72);
        patient.Role = "Patient";
        patient.Immunisations = new ArrayList<>();
        return patient;
    }

    public static Patient getNurse(){
        Patient nurse = new Patient();
        nurse.Id = Math.round(Math.random() * 1000);
        nurse.Name = "Mother Theresa";
        nurse.Age = (int)Math.round(Math.random() * 72);
        nurse.Role = "Nurse";
        return nurse;
    }

    private static String GetRandomName() {
        ArrayList<String> names = new ArrayList<>();
        names.add("Oprah Winfrey");
        names.add("Usain Bolt");
        names.add("Desmond Tutu");
        names.add("Hubert Cumberdale");
        names.add("Jemaine Clement");
        int index = (int)Math.floor(Math.random() * names.size());
        return names.get(index);
    }
}
