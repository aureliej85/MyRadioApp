package fr.media85.myradioapp.network;

import fr.media85.myradioapp.models.Attributes;
import retrofit2.Call;
import retrofit2.http.GET;

public interface WebRadioApi {

    @GET("current")
    Call<Attributes> getAttributesDetails();

}

