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

    //32 bit hash

    //sk_4125fb4da845306ee67dfcdfe1ce2d78 mommy
    //sk_8559578b2da4bb5242a02edf67a4f480 Sai Katakam
    //sk_d73ddba1c7fe0a4f0e015e2ab65de4e2 Deb
    //sk_7c6bdde81b696d38bbfad5955ef5c378 Chuintu
    //sk_7491efa11da81849de653d650c01d4ad Ashihsh phnnumber
    //sk_0dbf7cb4f9133bbadb5c9cbcdd4cb3b5 kec 9

    //sk_bb348ca304f4c35bf55d4f3485e94052 srisapete -- used on April 16th
    //sk_e0d29941dcaa55d5667de1102a2485a2 Ramesh -- used on April 16th
    //sk_73a66ccc9945ca5407d917b39f6118bb srisapet/Sapient@17 -- used on April 16th
    //sk_4125fb4da845306ee67dfcdfe1ce2d78 -- used on April 14th

    //sk_092d7d3827f06284fa592a44319fa1f7 - srisapet/Sapient@17 - team one
    //sk_350afb3ece969337716c2588cbfd419e - srisapet/Sapient@17 - team two
    //sk_d65f6e634493e0ea8a7b14e300a381f3 - srisapet/Sapient@17 - team three
    //sk_83896502960f585dcca1d73f35f3f517 - srisapet/Sapient@17 - team 4
    //sk_2b0a6a91fc2bb9461017e487f92f69e7 - srisapet/Sapient@17 - team mine
    //sk_21fd1288f442975a8c40ab5521c87715 - srisapet/Sapient@17 - team 5
    @Headers("Authorization: Bearer sk_9ace7323fba74819ddc443dc018a7fe6")
    @GET("v1/people/search")
    Observable<Contact> getContact(@Query("domain") String domain);

    ///v2/combined/find?email=stanprish@healthyline.com

    //https://person-stream.clearbit.com/v2/combined/find?email=stanprish@healthyline.com
    @Headers("Authorization: Bearer sk_4125fb4da845306ee67dfcdfe1ce2d78")//sk_bb348ca304f4c35bf55d4f3485e94052
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
