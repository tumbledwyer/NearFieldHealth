package org.jembi.mynfc;

import com.google.gson.Gson;

import org.jembi.mynfc.models.HealthCareUser;

public class JsonConverter {

    private static Gson gson;

    public static HealthCareUser convertToHealthCareUser(String rawJson){
        CreateGson();
        return gson.fromJson(rawJson, HealthCareUser.class);
    }

    private static void CreateGson() {
        if(gson == null){
            gson = new Gson();
        }
    }

}
