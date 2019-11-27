package com.sancheru.cleabitapi;/*package com.sancheru.myapplication;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uhealthcamp.model.ApiResponse;
import com.uhealthcamp.model.PatientHistoryCurrentExam;
import com.uhealthcamp.mvvm.ReportViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;*/

/**
 * Created by Ramesh on 25/12/18.
 */

/*public class PatientReportActivity extends BaseActivity {
    private ReportViewModel reportViewModel;
    @BindView(R.id.txt_date_from)
    DatePickerEditTextView txt_date_from;
    @BindView(R.id.txt_date_to)
    DatePickerEditTextView txt_date_to;

    @BindView(R.id.ll_contatiner)
    LinearLayout ll_contatiner;

    @BindView(R.id.btn_export)
    Button btn_export;
    //
    List<PatientHistoryCurrentExam> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Patient Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel.class);
        reportViewModel.getReportData().observe(this, new Observer<ApiResponse<List<PatientHistoryCurrentExam>>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<List<PatientHistoryCurrentExam>> listApiResponse) {
                if (listApiResponse.isLoading) {
                    showProgressDialog();
                } else if (listApiResponse.success != null) {
                    showToast(listApiResponse.success.size() + " Records found.");
                    hideProgressDialog();
                    setData(listApiResponse.success);
                } else if (listApiResponse.error != null) {
                    hideProgressDialog();
                }
            }
        });
        reportViewModel.loadReport(Helper.getTimeStr(Calendar.getInstance()), Helper.getTimeStr(Calendar.getInstance()));
        //
        txt_date_from.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                reportViewModel.loadReport(txt_date_from.getText().toString(), txt_date_to.getText().toString());
            }
        });

        txt_date_to.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                reportViewModel.loadReport(txt_date_from.getText().toString(), txt_date_to.getText().toString());
            }
        });

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExcel(items);
            }
        });

    }

    private void setData(List<PatientHistoryCurrentExam> items) {
        this.items = items;
        if (items == null || items.isEmpty()) {
            btn_export.setVisibility(View.GONE);
        } else {
            btn_export.setVisibility(View.VISIBLE);
        }
        ll_contatiner.removeAllViews();
        //
        View header = LayoutInflater.from(this).inflate(R.layout.row_report_1, null);
        ll_contatiner.addView(header);
        //
        int i = 0;
        for (PatientHistoryCurrentExam item : items) {
            View view = LayoutInflater.from(this).inflate(R.layout.row_report_1, null);
            //
            TextView nameView = view.findViewById(R.id.txt_name);
            nameView.setText(item.getPatientName());
            //
            TextView txt_hyper = view.findViewById(R.id.txt_hyper);
            txt_hyper.setText(item.getExam().getQuickCheck().isHyperTension() ? "Yes" : "No");
            //
            TextView txt_cholesterol = view.findViewById(R.id.txt_cholesterol);
            txt_cholesterol.setText(item.getExam().getQuickCheck().isCholestrol() ? "Yes" : "No");
            //
            TextView txt_diab = view.findViewById(R.id.txt_diab);
            txt_diab.setText(item.getExam().getQuickCheck().isDiabetes() ? "Yes" : "No");
            //
            TextView txt_tbd = view.findViewById(R.id.txt_tbd);
            txt_tbd.setText(item.getExam().getQuickCheck().isTbd() ? "Yes" : "No");
            //
            TextView txt_doctorid = view.findViewById(R.id.txt_doctorid);
            txt_doctorid.setText(item.getCreatedBy());
            //
            TextView txt_village = view.findViewById(R.id.txt_village);
            txt_village.setText(item.getPatientVillage());
            //
            TextView txt_date = view.findViewById(R.id.txt_date);
            txt_date.setText(item.getCreatedAt());
            //
            if (i % 2 == 0) {
                view.setBackgroundColor(Color.TRANSPARENT);
            } else {
                view.setBackgroundColor(Color.parseColor("#dddddd"));
            }
            i++;
            ll_contatiner.addView(view);
        }
    }

    private void saveExcel(List<PatientHistoryCurrentExam> items) {
        //
        File sd = new File(Environment.getExternalStorageDirectory(), "Download");
        String csvFile = "patient_history_" + new SimpleDateFormat("ddMMyyyy_hhmmss").format(new Date()) + ".xls";

        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            String value = new Gson().toJson(items);

            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("patient_medical_exam", 0);
            // column and row
            List<String> headers = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(value);
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
}*/

