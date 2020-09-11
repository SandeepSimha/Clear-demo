package com.sancheru.cleabitapi;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sancheru.cleabitapi.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ConcatMapOperatorActivity extends AppCompatActivity {

    private static final String TAG = ConcatMapOperatorActivity.class.getSimpleName();

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_concat_map);
        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Contact, Observable<Contact>>() {

                    @Override
                    public Observable<Contact> apply(Contact user) throws Exception {

                        // getting each user address by making another network call
                        return getAddressObservable(user);
                    }
                })
                .subscribe(new Observer<Contact>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(Contact user) {
                        //Log.e(TAG, "onNext: " + user.getName() + ", " + user.getGender() + ", " + user.getAddress().getAddress());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "All users emitted!");
                    }
                });
    }

    /**
     * Assume this as a network call
     * returns Users with address filed added
     */
    private Observable<Contact> getAddressObservable(final Contact user) {

        final String[] addresses = new String[]{
                "1600 Amphitheatre Parkway, Mountain View, CA 94043",
                "2300 Traverwood Dr. Ann Arbor, MI 48105",
                "500 W 2nd St Suite 2900 Austin, TX 78701",
                "355 Main Street Cambridge, MA 02142"
        };

        return Observable.create(new ObservableOnSubscribe<Contact>() {
                    @Override
                    public void subscribe(ObservableEmitter<Contact> emitter) throws Exception {
                        Contact address = new Contact();
                        //address.setAddress(addresses[new Random().nextInt(2) + 0]);
                        if (!emitter.isDisposed()) {
                            //user.setAddress(address);


                            // Generate network latency of random duration
                            int sleepTime = new Random().nextInt(1000) + 500;

                            Thread.sleep(sleepTime);
                            emitter.onNext(user);
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    /**
     * Assume this is a network call to fetch users
     * returns Users with name and gender but missing address
     */
    private Observable<Contact> getUsersObservable() {
        String[] maleUsers = new String[]{"Mark", "John", "Trump", "Obama"};

        final List<Contact> users = new ArrayList<>();

        for (String name : maleUsers) {
            Contact user = new Contact();
            //user.setName(name);
            //user.setGender("male");

            users.add(user);
        }

        return Observable
                .create(new ObservableOnSubscribe<Contact>() {
                    @Override
                    public void subscribe(ObservableEmitter<Contact> emitter) throws Exception {
                        for (Contact user : users) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}