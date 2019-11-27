package com.sancheru.cleabitapi;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.sancheru.cleabitapi.model.Contact;
import com.sancheru.cleabitapi.model.DomainModel;
import com.sancheru.cleabitapi.model.EmailLookup;
import com.sancheru.cleabitapi.model.Person;
import com.sancheru.cleabitapi.model.Results;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    List<String> queryString = Arrays.asList("editior", "content manager", "content", "manager", "ceo", "executive vp");

    List<Person> personList;
    List<String> domainList;
    List<DomainModel> domainModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        personList = new ArrayList<>();
        domainList = new ArrayList<>();
        domainModelList = new ArrayList<>();

        /*streamFetchContactWithDomain("fitnessformulary.com")
                .map(new Function<Contact, List<Results>>() {
                    @Override
                    public List<Results> apply(Contact contact) throws Exception {

                        return contact.getResults();
                    }
                }).subscribe(this::handleResults, this::handleError);*/

        /*streamFetchPersonWithEmail("dan.lynch@fitnessformulary.com")
                .map(new Function<EmailLookup, EmailLookup>() {
                    @Override
                    public EmailLookup apply(EmailLookup person) throws Exception {
                        //Log.e("MainActivity", "person = " + person);
                        return person;
                    }
                }).subscribe(this::handleResults, this::handleError);*/

        readExcelData();

        for (int i = 0; i < 1; i++) {
            Log.e("MainActivity", "apply me " + domainList.get(i));
            feedDatafromDomainList(domainList.get(i));
        }

        sleepForsomeTime();
    }

    private void feedDatafromDomainList(String domain) {
        //feed the domain data from excel sheet for each cell
        Observable<EmailLookup> detailObservables = streamFetchContactWithDomain(domain)
                .map(contact -> {
                    List<Results> listOfResults = new ArrayList<>();

                    String social = "";
                    HashMap<String, Results> resultsStringHashMap = new HashMap<>();

                    if (contact.getResults().size() >= 2) {
                        for (Results res : contact.getResultsArray()) {
                            //sort the array based on the title
                            if (res.getTitle() != null) {
                                resultsStringHashMap.put(res.getTitle(), res);
                            }
                        }

                        if (!resultsStringHashMap.isEmpty()) {
                            List<String> sortedTitles = compareStringsandSort(queryString, resultsStringHashMap);

                            Results res = resultsStringHashMap.get(sortedTitles.get(0));
                            listOfResults.add(res);

                            /*Log.e("MainActivity", "person data = " + domain + "\t" + res.getName().getFirstName() + "\t" +
                                    res.getName().getLastName() + "\t" + res.getEmail() + "\t" + res.getTitle() + "\t" + social + "\t");*/
                        } else { //all of the contacts has an null titles
                            Results r1 = (Results) contact.getResults().get(0);
                            listOfResults.add(r1);

                            /*Log.e("MainActivity", "person data = " + domain + "\t" + r1.getName().getFirstName() + "\t" +
                                    r1.getName().getLastName() + "\t" + r1.getEmail() + "\t" + r1.getTitle() + "\t" + social + "\t");*/
                        }

                    } else if (contact.getResults().size() == 0) {
                        Log.e("MainActivity", "person data map = " + domain + "\t" + "\t" + "\t" +
                                "\t" + "\t" + "\t" + "\t" + "\t" + "\t" + "" + "\t" + "No Result");
                    } else {
                        /*Log.e("MainActivity", "person data = " + domain + "\t" + res.getName().getFirstName() + "\t" +
                                    res.getName().getLastName() + "\t" + res.getEmail() + "\t" + res.getTitle() + "\t" + social + "\t");*/
                        listOfResults.addAll(Arrays.asList(contact.getResultsArray()));
                    }
                    return listOfResults;
                })
                .flatMap((Function<List<Results>, ObservableSource<Results>>) results -> Observable.fromIterable(results))
                .flatMap((Function<Results, ObservableSource<EmailLookup>>) results -> {
                    //Log.e("MainActivity", "apply 3");
                    //Log.e("MainActivity", "apply 3 " + results.getEmail());
                    return streamFetchPersonWithEmail(results.getEmail());
                });

        detailObservables.subscribe(new Observer<EmailLookup>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(EmailLookup person) {
                String social = "";

                if (person.getP().getTwitter().getHandle() != null) {
                    social = "https://twitter.com/" + person.getP().getTwitter().getHandle();
                } else if (person.getP().getFacebook().getHandle() != null) {
                    social = "https://www.facebook.com/" + person.getP().getFacebook().getHandle();
                } else if (person.getP().getLinkedin().getHandle() != null) {
                    social = "https://www.linkedin.com/" + person.getP().getLinkedin().getHandle();
                }

                //Log.e("MainActivity", "person data = email" + person.getP().getEmail() + "Domain " + domain + ", social " + social);
                Log.e("MainActivity", "person data = " + domain + "\t" + person.getP().getName().getFirstName() + "\t" +
                        person.getP().getName().getLastName() + "\t" + person.getP().getEmail() + "\t" + person.getP().getEmployment().getTitle() + "\t" + social + "\t");
            }

            @Override
            public void onError(Throwable e) {
                //Log.e("MainActivity", "error = " + e);
            }

            @Override
            public void onComplete() {
                //saveExcel(domainModelList);

                //Log.e("MainActivity", "person data = " + personList.get(0).getName().getFirstName());
            }
        });
    }

    private void sleepForsomeTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < personList.size(); i++) {
                    Log.e("MainActivity", "person data = domainModelList " + personList.get(i).getEmail());
                }
                //saveExcel(domainModelList);
            }
        }, 500);
    }

    //"fitnessformulary.com"
    public Observable<Contact> streamFetchContactWithDomain(String domain) {
        return ClearbitDomainService.domainRetrofit.create(ClearbitDomainService.class).getContact(domain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 1 - Create a stream that will get user infos on Github API
    public Observable<EmailLookup> streamFetchPersonWithEmail(String email) {
        return ClearbitDomainService.personRetrofit.create(ClearbitDomainService.class).getPersonInformation(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void handleResults(List<Results> marketList) {
        Toast.makeText(this, "RESULTS FOUND",
                Toast.LENGTH_LONG).show();
        for (Results results : marketList) {
            //Log.e("MainActivity", " " + results.getName().getFirstName());
        }
    }

    private void handleError(Throwable t) {
        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again = " + t,
                Toast.LENGTH_LONG).show();
        Log.e("MainActivity", "error = " + t);
    }

    @SuppressLint("CheckResult")
    private void callEndpoints() {

        /*ClearbitDomainService clearbitDomainService = retrofit.create(ClearbitDomainService.class);

        //Single call
        Observable<List<Results>> clearbitObservable = clearbitDomainService.getContact("fitnessformulary.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);

        // 1 - Create a stream that will get person infos on Clearbit API
        ClearbitDomainService clearbitDomainService1 = retrofit.create(ClearbitDomainService.class);
        return gitHubService.getUserInfos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
*/

        /*cryptoObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<Results>, Object>() {
                    @Override
                    public Object apply(List<Results> results) throws Exception {
                        for (Results results1 : results) {
                            Log.e("MainActivity", " " + results1.getName().getFamilyName());
                            return results1.getEmail();
                        }
                        return "";
                    }
                })
                *//*.map(new Function<Contact, String>() {
                    @Override
                    public String apply(Contact contact) throws Exception {
                        if (contact.getResults() != null || !contact.getResults().isEmpty()) {
                            return contact.getResults().get(0).getEmail();
                        }
                        return "";
                    }
                })
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return null;
                    }
                })*//*
                .subscribe(this::handleResults, this::handleError);*/

        /*Observable<List<Crypto.Market>> btcObservable = clearbitDomainService.getCoinData("btc")
                .map(result -> Observable.fromIterable(result.ticker.markets))
                .flatMap(x -> x).filter(y -> {
                    y.coinName = "btc";
                    return true;
                }).toList().toObservable();

        Observable<List<Crypto.Market>> ethObservable = clearbitDomainService.getCoinData("eth")
                .map(result -> Observable.fromIterable(result.ticker.markets))
                .flatMap(x -> x).filter(y -> {
                    y.coinName = "eth";
                    return true;
                }).toList().toObservable();

        Observable.merge(btcObservable, ethObservable)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError);*/
    }

    private void saveExcel(List<DomainModel> personList) {
        //
        File sd = new File(Environment.getExternalStorageDirectory(), "Download");
        String csvFile = "person_history_" + new SimpleDateFormat("ddMMyyyy_hhmmss").format(new Date()) + ".xls";

        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            String value = new Gson().toJson(personList);

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Sheet - Susmitha", 0);

            // column and row
            List<String> headers = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(value);

            /*JSONObject jsonObject = new JSONObject(value);

            List<Label> list = new ArrayList<>();

            createLabel("", headers, list, +1, new AtomicInteger(), jsonObject);

            for (Label l : list) {
                sheet.addCell(l);
            }*/


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                List<Label> list = new ArrayList<>();

                createLabel("", headers, list, i + 1, new AtomicInteger(), jsonObject);

                for (Label l : list) {
                    sheet.addCell(l);
                }
            }
            //
            for (int i = 0; i < headers.size(); i++) {
                sheet.addCell(new Label(i, 0, headers.get(i)));
            }
            //
            workbook.write();
            workbook.close();
            Toast.makeText(getApplication(), "Please Check Downloads Folder. File name: " + new File(csvFile).getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "Failed to export", Toast.LENGTH_SHORT).show();
        }
    }

    public void createLabel(String prefix, List<String> headers, List<Label> labels, int row, AtomicInteger col, JSONObject jsonObject) throws JSONException {
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();

            String pref = TextUtils.isEmpty(prefix) ? key : prefix + "_" + key;
            Log.e("Ramesh", "Key:" + pref + "- " + col.toString());
            if (!(jsonObject.get(key) instanceof JSONObject)) {
                if (!headers.contains(pref)) {
                    headers.add(pref);
                }
            }
            //
            if (jsonObject.get(key) instanceof String) {
                labels.add(new Label(col.getAndIncrement(), row, jsonObject.getString(key)));
            } else if (jsonObject.get(key) instanceof JSONObject) {
                createLabel(pref, headers, labels, row, col, jsonObject.getJSONObject(key));
            } else if (jsonObject.get(key) instanceof Long) {
                labels.add(new Label(col.getAndIncrement(), row, String.valueOf(jsonObject.getLong(key))));
            } else if (jsonObject.get(key) instanceof Double) {
                labels.add(new Label(col.getAndIncrement(), row, String.valueOf(jsonObject.getDouble(key))));
            } else if (jsonObject.get(key) instanceof Boolean) {
                labels.add(new Label(col.getAndIncrement(), row, String.valueOf(jsonObject.optBoolean(key, false) ? "Yes" : "No")));
            }
        }
    }

    private void readExcelData() {
        AssetManager am = getAssets();
        try {
            InputStream inputStream = am.open("sandeep.xls");
            //InputStream inputStream = new FileInputStream(inputFile);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            //outter loop, loops through rows
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                //inner loop, loops through columns
                for (int c = 0; c < cellsCount; c++) {
                    //handles if there are to many columns on the excel sheet.
                    if (c > 2) {
                        Log.e(TAG, "readExcelData: ERROR. Excel File Format is incorrect! ");
                        //toastMessage("ERROR: Excel File Format is incorrect!");
                        break;
                    } else {
                        String value = getCellAsString(row, c, formulaEvaluator);
                        String cellInfo = "r:" + r + "; c:" + c + "; v:" + value;
                        sb.append(value + ", ");
                    }
                }
                sb.append(":");
            }
            parseStringBuilder(sb);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "readExcelData: FileNotFoundException. " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "readExcelData: Error reading inputstream. " + e.getMessage());
        }
    }

    /**
     * Returns the cell as a string from the excel file
     *
     * @param row
     * @param c
     * @param formulaEvaluator
     * @return
     */
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = "" + cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = "" + numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = "" + cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {

            Log.e(TAG, "getCellAsString: NullPointerException: " + e.getMessage());
        }
        return value;
    }

    /**
     * Method for parsing imported data and storing in ArrayList<XYValue>
     */
    public void parseStringBuilder(StringBuilder mStringBuilder) {
        // splits the sb into rows.
        String[] rows = mStringBuilder.toString().split(":");

        //Add to the ArrayList<XYValue> row by row
        for (String row : rows) {
            //Split the columns of the rows
            String[] columns = row.split(",");
            domainList.add(columns[0]);
        }
    }


    private List<String> compareStringsandSort(List<String> queryString, List<String> sortString) {
        List<String> A3result = new ArrayList<>();
        HashMap<String, Integer> hashMap = new HashMap<>();
        for (String role : sortString) {
            if (hashMap.containsKey(role)) {
                hashMap.put(role, hashMap.get(role) + 1);
            } else
                hashMap.put(role, 1);
        }
        for (String role : queryString) {
            if (hashMap.containsKey(role)) {
                //put in o/p arry and remove number from hashmp
                A3result.add(role);
                hashMap.remove(role);
            }
        }

        for (Map.Entry mapElement : hashMap.entrySet()) {
            A3result.add(mapElement.getKey().toString());
        }
        return A3result;
    }

    private List<String> compareStringsandSort(List<String> queryString, HashMap<String, Results> sortString) {

        ArrayList<String> sortedKeys = new ArrayList<String>(sortString.keySet());
        return compareStringsandSort(queryString, sortedKeys);
    }
}
