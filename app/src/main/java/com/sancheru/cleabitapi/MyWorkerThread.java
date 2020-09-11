package com.sancheru.cleabitapi;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

class MyWorkerThread extends AsyncTask<Object, Void, List<String>> {

    List<String> domainList = new ArrayList<>();

    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JsonFactory factory = JacksonFactory.getDefaultInstance();
    final Sheets sheetsService = new Sheets.Builder(transport, factory, null)
            .setApplicationName("My Awesome App")
            .build();
    final String spreadsheetId = Config.spreadsheet_id;
    public AsyncResponse delegate;

    MyWorkerThread(final AsyncResponse listener) {
        delegate = listener;
    }

    @Override
    protected List<String> doInBackground(Object... objects) {
        try {
            String range = "Sheet1!A1:A201";//Sheet1!A1:B2//TODO:// take input from keyboard
            ValueRange result = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .setKey(Config.google_api_key)
                    .execute();
            if (result.getValues() != null) {
                for (int i = 0; i < result.getValues().size(); i++) {
                    domainList.add(String.valueOf(result.getValues().get(i).get(0)));
                }
                //Log.e(TAG, "SUCCESS: rows retrieved " + domainList.size());
            }
        } catch (IOException e) {
            Log.e(TAG, "Sheets failed - " + e.getLocalizedMessage());
        }
        return domainList;
    }

    @Override
    protected void onPostExecute(List<String> domainList) {
        super.onPostExecute(domainList);
        delegate.processFinish(domainList);
    }
}
