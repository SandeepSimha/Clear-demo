package com.sancheru.cleabitapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sancheru.cleabitapi.model.Contact;
import com.sancheru.cleabitapi.model.EmailLookup;
import com.sancheru.cleabitapi.model.Person;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ClearbitDomainService {


    //https://person-stream.clearbit.com/
    String BASE_URL = "https://prospector.clearbit.com/";
    String PERSON_BASE_URL = "https://person-stream.clearbit.com/";

    //sk_73a66ccc9945ca5407d917b39f6118bb
    //v1/people/search?domain=fitnessformulary.com
    @Headers("Authorization: Bearer sk_bb348ca304f4c35bf55d4f3485e94052")//sk_bb348ca304f4c35bf55d4f3485e94052
    @GET("v1/people/search")
    Observable<Contact> getContact(@Query("domain") String domain);

    ///v2/combined/find?email=stanprish@healthyline.com

    //https://person-stream.clearbit.com/v2/combined/find?email=stanprish@healthyline.com
    @Headers("Authorization: Bearer sk_bb348ca304f4c35bf55d4f3485e94052")//sk_bb348ca304f4c35bf55d4f3485e94052
    @GET("v2/combined/find")
    Observable<EmailLookup> getPersonInformation(@Query("email") String email);

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit personRetrofit = new Retrofit.Builder()
            .baseUrl(PERSON_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    Retrofit domainRetrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
}
