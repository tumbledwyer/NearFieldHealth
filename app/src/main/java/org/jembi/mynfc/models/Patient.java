package org.jembi.mynfc.models;

import java.util.List;

public class Patient extends HealthCareUser{
    public int Age;
    public boolean Married;
    public List<Immunisation> Immunisations;
}
