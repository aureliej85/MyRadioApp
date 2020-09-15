package fr.media85.myradioapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebRadioClient {

    public static final String BASE_URL = "https://api.radioking.io/widget/radio/paradise-radio/track/"; //here the base link of the json file
    public static Retrofit retrofit = null;

    public static Retrofit getAttributes(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

}
