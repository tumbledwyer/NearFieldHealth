package org.jembi.mynfc;

import android.content.Context;

import org.jembi.mynfc.models.Patient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class FileSystem {
    public static void write(Context context, Patient patient) throws IOException {
        String fileName = String.format("%d_%s", patient.Id, patient.Name);
        String jsonPatient = JsonConverter.convertToJson(patient);

        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(jsonPatient.getBytes());
        fos.close();
    }

    public static Patient read(Context context, Patient patient) throws IOException {
        String fileName = String.format("%d_%s", patient.Id, patient.Name);
        FileInputStream fileInputStream = context.openFileInput(fileName);
        String buff = new Scanner(fileInputStream).useDelimiter("\\Z").next();
        return JsonConverter.convertToPatient(buff.toString());
    }
}
