package org.jembi.nearFieldHealth.models;

import java.io.Serializable;
import java.util.Date;

public class Immunisation implements Serializable {
    public Date Date;
    public String Type;
    public HealthCareWorker HealthCareWorker;
}
