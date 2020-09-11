package com.sancheru.cleabitapi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sancheru.cleabitapi.model.Contact;
import com.sancheru.cleabitapi.model.Domain;
import com.sancheru.cleabitapi.model.EmailLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class UnUsed {
    private static final String TAG = "MainActivity";
    private Context mContext;

    public UnUsed(MainActivity mainActivity) {
        mContext = mainActivity;
    }

    @Deprecated
    public Observable<Contact> streamFetchContactWithDomain(String domain) {
        return ClearbitDomainService.domainRetrofit.create(ClearbitDomainService.class).getContact(domain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Deprecated
    private void addItemToSheet(String domainName, String domainData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://script.google.com/macros/s/AKfycbyunmXXZZwR8qKYypnlEnDAOllACVCzc881IMjFuh3cKxFokKc/exec",
                response -> {
                    Log.e(TAG, "\t" + response);
                },
                error -> {
                    Log.e(TAG, "" + error.getLocalizedMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addItem");
                parmas.put("itemName", domainName);
                parmas.put("brand", domainData);
                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }

    public Observable<EmailLookup> streamFetchPersonWithEmail(String email) {
        return ClearbitDomainService.personRetrofit.create(ClearbitDomainService.class).getPersonInformation(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*private void feedData3(String domain) {
        Domain domain1 = new Domain();
        streamFetchContactWithDomainList(domain, keyText.getText().toString())
                //flatMap ot Concat
                .flatMap((Function<Contact, ObservableSource<?>>) contact -> {
                    if (contact.getResults().size() == 0) {
                        domain1.setDomain(domain);
                        domain1.setDomainData(" ");
                    } else {
                        List<String> emailList = new ArrayList<>();
                        for (int i = 0; i < contact.getResults().size(); i++) {
                            emailList.add(contact.getResults().get(i).getEmail());
                        }
                        domain1.setDomain(domain);
                        domain1.setDomainData(emailList.toString());
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "Data upload to sheet", Toast.LENGTH_SHORT).show();
                    return streamFetchDomainListContacts(domain1.getDomain(), domain1.getDomainData());
                    //return (ObservableSource<?>) domain1;
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, domain + ",Success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, domain + ",failure = " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                        processSucess();
                    }
                });
    }*/

    private void feedmyData(List<String> domainList) {
        Observable.fromIterable(domainList)
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<String, ObservableSource<Domain>>() {
                    @Override
                    public ObservableSource<Domain> apply(String domain) throws Exception {
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Domain>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Domain o) {
                        Log.e(TAG, o.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Domain> getAddressObservable(final Domain user) {
        return streamFetchContactWithDomain(user.getDomain())
                .map(new Function<Contact, Domain>() {
                    @Override
                    public Domain apply(Contact contact) throws Exception {
                        if (contact.getResults().size() == 0) {
                            //Log.e(TAG, domain + ",NO RESULT");
                            //sendPost(domain, "NO RESULT");
                            user.setDomain(user.getDomain());
                            user.setDomainData("NO RESULT");
                        } else {
                            List<String> emailList = new ArrayList<>();
                            for (int i = 0; i < contact.getResults().size(); i++) {
                                emailList.add(contact.getResults().get(i).getEmail());
                            }
                            //Log.e(TAG, domain + ", size = " + emailList.size());
                            //addItemToSheet(domain, emailList.toString());
                            //sendPost(domain, emailList.toString());
                            user.setDomain(user.getDomain());
                            user.setDomainData(emailList.toString());
                        }
                        return user;
                    }
                })/*.concatMap(new Function<Domain, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Domain domain) throws Exception {
                        return sendPost(domain.getDomain(), domain.getDomainData());
                    }
                })*/;

       /* return Observable
                .create(new ObservableOnSubscribe<Domain>() {
                    @Override
                    public void subscribe(ObservableEmitter<Domain> emitter) throws Exception {
                        Log.e(TAG, "domain" + user.getDomain());
                        if (!emitter.isDisposed()) {

                            user.setAddress(address);


                            // Generate network latency of random duration
                            int sleepTime = new Random().nextInt(1000) + 500;

                            Thread.sleep(sleepTime);
                            emitter.onNext(user);
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());*/
    }

    private ObservableSource<? extends String> feedData(String domain) {
        streamFetchContactWithDomain(domain)
                .map(new Function<Contact, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Contact contact) throws Exception {
                        Log.e(TAG, "" + domain + contact.getResults().size());
                        Observable<String> o1 = Observable.just(domain);
                        return Observable.just("Value is: " + domain + "domain = " + o1);
                    }
                })
                .subscribe();
        return null;
    }

    private void feedData2(String domain) {
        streamFetchContactWithDomain(domain).subscribe(new Observer<Contact>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Log.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Contact contact) {
                if (contact.getResults().size() == 0) {
                    Log.e(TAG, domain + ",NO RESULT");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //streamFetchDomainListContacts(domain, "NO RESULT");
                } else {
                    List<String> emailList = new ArrayList<>();
                    for (int i = 0; i < contact.getResults().size(); i++) {
                        emailList.add(contact.getResults().get(i).getEmail());
                    }
                    //TODO:// write the data to Sheets
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, domain + ", size = " + emailList.size());
                    //streamFetchDomainListContacts(domain, emailList.toString());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, domain + "\t" + e.getMessage());
            }

            @Override
            public void onComplete() {
                //Log.e(TAG, "All users emitted!" + domain);
            }
        });
    }

}
