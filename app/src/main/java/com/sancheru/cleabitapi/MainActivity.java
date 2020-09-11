package com.sancheru.cleabitapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sancheru.cleabitapi.model.Contact;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private static final String TAG = "MainActivity";

    private EditText fromText;
    private EditText toText;
    private EditText keyText;
    private MyWorkerThread asyncTask;
    private ProgressBar mProgressBar;
    private List<String> mDomainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        fromText = findViewById(R.id.editText_from);
        toText = findViewById(R.id.editText2_to);
        keyText = findViewById(R.id.editText_key);
        mProgressBar = findViewById(R.id.progressBar);

        fab.setOnClickListener(view -> {

            if (TextUtils.isEmpty(fromText.getText().toString().trim())) {
                fromText.setFocusable(true);
                fromText.setError("Please give some number");
                return;
            }

            if (TextUtils.isEmpty(toText.getText().toString().trim())) {
                toText.setFocusable(true);
                toText.setError("Please give some number");
                return;
            }

            if (Integer.parseInt(toText.getText().toString().trim()) < Integer.parseInt(fromText.getText().toString().trim())) {
                Toast.makeText(this, "Please enter correct numbers ", Toast.LENGTH_SHORT).show();
                return;
            }

            mProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (asyncTask == null) {
                asyncTask = new MyWorkerThread(this);
            }

            if (asyncTask.getStatus() != AsyncTask.Status.RUNNING && mDomainList == null) {   // check if asyncTasks is running
                asyncTask.cancel(true); // asyncTasks not running => cancel it
                asyncTask = new MyWorkerThread(this); // reset task
                asyncTask.execute(); // execute new task (the same task)
            } else {
                publishData(mDomainList);
            }
        });

        new UnUsed(this);
    }

    @Override
    public void processFinish(List<String> domainList) {
        mDomainList = domainList;
        publishData(mDomainList);
    }

    private void publishData(List<String> domainList){
        Log.e(TAG, "SUCCESS: " + domainList.size());
        mProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        List<Observable<?>> requests = new ArrayList<>();

        //TODO:give the range from in/p
        int from = Integer.parseInt(fromText.getText().toString().trim()) - 1;
        int to = Integer.parseInt(toText.getText().toString().trim()) - 1;

        for (int i = from; i <= to; i++) {
            if (domainList.size() != 0) {
                Log.e(TAG, "domainList: " + domainList.get(i));
                requests.add(streamFetchContactWithDomainList(domainList.get(i), keyText.getText().toString()));
                //feedData(domainList.get(i));
                //feedData2(domainList.get(i));
                //feedData3(domainList.get(i));//IMPORTANT
                //jaffa(domainList.get(i));
                //jaffa2(domainList.get(i));
            } else {
                Log.e(TAG, "SUCCESS: rows retrieved " + domainList.size());
            }
        }

        zipData(requests);
    }

    private void zipData(List<Observable<?>> requests) {
        List<String> emailData = new ArrayList<>();
        Observable.zip(requests, objects -> {
            for (Object object : objects) {
                int a = ((Contact) object).getResults().toArray().length;
                List<String> emailData2 = new ArrayList<>();
                if (a == 0) {
                    emailData2.add("NULL");
                } else {
                    for (int k = 0; k < ((Contact) object).getResults().size(); k++) {
                        String email = ((Contact) object).getResults().get(k).getEmail();
                        emailData2.add(email);
                    }
                }
                Log.e(TAG, "email data = " + emailData.size());
                emailData.add(emailData2.toString());

                StringBuilder builder = new StringBuilder();
                for (String value : emailData2) {
                    builder.append(value).append(", ");
                }
                String text = builder.toString();

                //Delay 2.5ms
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //The below Method to upload the Email string builder to google sheet
                streamUploadEmailData("sandeep", text.substring(0, text.length() - 2))
                        .flatMap(new Function<String, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(String s) throws Exception {
                                return null;
                            }
                        })
                        .subscribe(new Observer<Object>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Object o) {
                                //Log.e(TAG, "streamFetchDomainListContacts - onNext = " + o);
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Log.e(TAG, "streamFetchDomainListContacts - onError= " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
            return emailData;
        }).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<String> strings) {
                Log.e(TAG, "Total emails sent to host = " + strings.size());
                processSuccess();
                //finish Loading screen
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "email data = " + e.getLocalizedMessage());
                processFailure(e.getLocalizedMessage());
                //finish Loading screen
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public Observable<Contact> streamFetchContactWithDomainList(String domain, String authToken) {
        return ClearbitDomainService.domainRetrofit.create(ClearbitDomainService.class).getContactList(domain, "Bearer " + authToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> streamUploadEmailData(String domainName, String domainData) {
        return ClearbitDomainService.retrofit.create(ClearbitDomainService.class)
                .savePost("addItem", domainName, domainData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void processFailure(String s) {
        Toast.makeText(this, "Data not posted to sheets" + s, Toast.LENGTH_SHORT).show();
    }

    public void processSuccess() {
        Toast.makeText(this, "Data has been posted to sheets", Toast.LENGTH_SHORT).show();
    }
}
