package com.example.demodatabase.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.demodatabase.R;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.User;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;


public class GraphFragment extends Fragment {
    HorizontalBarChart barChart,studySetsBarChart;

    FirebaseFirestore database;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<StudySet> studySets = new ArrayList<>();
    int currentYear;
    EditText etYear;
    TextView title;
    Spinner spinner;
    private static final String[] paths = {"Study Set", "Users"};

    void initUI(View view) {
        barChart = view.findViewById(R.id.barChart);
        studySetsBarChart = view.findViewById(R.id.studySetBarChat);
        etYear = view.findViewById(R.id.et_year);
        title = view.findViewById(R.id.title);
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        initDataForStudySets(parseDate(currentYear + "-01-01"));
                        studySetsBarChart.setVisibility(View.VISIBLE);
                        barChart.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        initData(parseDate(currentYear + "-01-01"));
                        studySetsBarChart.setVisibility(View.INVISIBLE);
                        barChart.setVisibility(View.VISIBLE);
                        break;




                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    void initCreatedUserChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        LinkedHashMap<String, Integer> countByMonth = new LinkedHashMap<>();
        countByMonth.put("0", 0);
        countByMonth.put("1", 0);
        countByMonth.put("2", 0);
        countByMonth.put("3", 0);
        countByMonth.put("4", 0);
        countByMonth.put("5", 0);
        countByMonth.put("6", 0);
        countByMonth.put("7", 0);
        countByMonth.put("8", 0);
        countByMonth.put("9", 0);
        countByMonth.put("10", 0);
        countByMonth.put("11", 0);

        for (User user : users) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(user.getCreatedDate());
            String month = String.valueOf(cal.get(Calendar.MONTH));// Extract year-month
            Log.d("month", "getMonthIndex: " + month);

            if (countByMonth.containsKey(month)) {
                countByMonth.put(month, countByMonth.get(month) + 1);
            } else {
                countByMonth.put(month, 1);
            }
        }


        ValueFormatter intFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        };


        ArrayList<String> labels = new ArrayList<>();
        for (String key : countByMonth.keySet()) {
            Log.d("month", "initCreatedUserChart: " + intToMonth(Integer.parseInt(key)));
            int count = countByMonth.get(key);
            labels.add(intToMonth(Integer.parseInt(key)));
            entries.add(new BarEntry(Float.parseFloat(key), count));
        }

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);


        BarDataSet dataSet = new BarDataSet(entries, "Number of Users Created by Month in 2023");
        dataSet.setValueTextSize(16f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(intFormatter);
        BarData data = new BarData(dataSet);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);
        yAxis.setAxisMinimum(0f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setValueFormatter(intFormatter);

        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        barChart.getXAxis().setDrawGridLines(false);


        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(labels));

        xAxis.setLabelCount(entries.size());
        yAxis.setValueFormatter(intFormatter);

        barChart.setData(data);
        if (barChart.getYMax() < 10) {
            rightAxis.setGranularity(1f);
        } else {
            rightAxis.setGranularity(Math.floorDiv(entries.size(), 2));
        }

        barChart.invalidate();
        barChart.getLegend().setEnabled(false);
    }

    String intToMonth(int monthNumber) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (monthNumber >= 0 && monthNumber < monthNames.length) {
            String monthName = monthNames[monthNumber];
            return monthName;
        } else {
            return "Invalid";
        }

    }

    void onDataLoadedForStudySets(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        LinkedHashMap<String, Integer> countByMonth = new LinkedHashMap<>();
        countByMonth.put("0", 0);
        countByMonth.put("1", 0);
        countByMonth.put("2", 0);
        countByMonth.put("3", 0);
        countByMonth.put("4", 0);
        countByMonth.put("5", 0);
        countByMonth.put("6", 0);
        countByMonth.put("7", 0);
        countByMonth.put("8", 0);
        countByMonth.put("9", 0);
        countByMonth.put("10", 0);
        countByMonth.put("11", 0);

        for (StudySet s : studySets) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(s.getDate());
            String month = String.valueOf(cal.get(Calendar.MONTH));// Extract year-month
            Log.d("month", "getMonthIndex: " + month);

            if (countByMonth.containsKey(month)) {
                countByMonth.put(month, countByMonth.get(month) + 1);
            } else {
                countByMonth.put(month, 1);
            }
        }

        ValueFormatter intFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        };


        ArrayList<String> labels = new ArrayList<>();
        for (String key : countByMonth.keySet()) {

            int count = countByMonth.get(key);
            labels.add(intToMonth(Integer.parseInt(key)));
            entries.add(new BarEntry(Float.parseFloat(key), count));
        }

        studySetsBarChart.setDrawBarShadow(false);
        studySetsBarChart.setDrawValueAboveBar(true);
        studySetsBarChart.getDescription().setEnabled(false);
        studySetsBarChart.setPinchZoom(false);
        studySetsBarChart.setDrawGridBackground(false);


        BarDataSet dataSet = new BarDataSet(entries, "Number of Users Created by Month in 2023");
        dataSet.setValueTextSize(16f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(intFormatter);
        BarData data = new BarData(dataSet);
        XAxis xAxis = studySetsBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        YAxis yAxis = studySetsBarChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);
        yAxis.setAxisMinimum(0f);

        YAxis rightAxis = studySetsBarChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setValueFormatter(intFormatter);

        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        studySetsBarChart.getXAxis().setDrawGridLines(false);


        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        studySetsBarChart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(labels));

        xAxis.setLabelCount(entries.size());
        yAxis.setValueFormatter(intFormatter);

        studySetsBarChart.setData(data);
        if (studySetsBarChart.getYMax() < 10) {
            rightAxis.setGranularity(1f);
        } else {
            rightAxis.setGranularity(Math.floorDiv(entries.size(), 2));
        }

        studySetsBarChart.invalidate();
        studySetsBarChart.getLegend().setEnabled(false);
    }

    void initDataForStudySets(Date date){
        studySets.clear();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        currentYear = cal.get(Calendar.YEAR);
        title.setText("Number of studySets created in " + currentYear);
        String startDateStr = cal.get(Calendar.YEAR) + "-01-01";
        String endDateStr = cal.get(Calendar.YEAR) + "-12-31";
        Date startDate = parseDate(startDateStr);
        Date endDate = parseDate(endDateStr);
        database.collection("studySets")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot d : task.getResult()
                            ) {
                                StudySet u = d.toObject(StudySet.class);
                                studySets.add(u);
                            }
                        }
                        onDataLoadedForStudySets();
                    }
                });

    }



    void initData(Date date) {
        users.clear();
        database = FirebaseFirestore.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        currentYear = cal.get(Calendar.YEAR);
        title.setText("Number of users created in " + currentYear);
        String startDateStr = cal.get(Calendar.YEAR) + "-01-01";
        String endDateStr = cal.get(Calendar.YEAR) + "-12-31";
        Date startDate = parseDate(startDateStr);
        Date endDate = parseDate(endDateStr);
        database.collection("users")
                .whereGreaterThanOrEqualTo("createdDate", startDate)
                .whereLessThanOrEqualTo("createdDate", endDate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot d : task.getResult()
                            ) {
                                User u = d.toObject(User.class);
                                users.add(u);
                            }
                        }
                        initCreatedUserChart();
                    }
                });



    }

    boolean checkEmpty(EditText view) {
        if (view.getText().toString().equals("")) {
            etYear.setError("Please fill in the year");
            return false;
        }
        return true;
    }

    void bindingAction() {



        etYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkEmpty(etYear)) {
                    if(barChart.getVisibility() == View.VISIBLE){
                        initData(parseDate(etYear.getText().toString() + "-01-01"));
                    }else{
                        initDataForStudySets(parseDate(etYear.getText().toString() + "-01-01"));
                    }
                }
            }
        });
    }



    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        initUI(view);
        initData(new Date());
        bindingAction();
        return view;
    }
}