package com.example.demodatabase;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demodatabase.model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminHomeActivity extends AppCompatActivity {
    BarChart barChart;
    FirebaseFirestore database;
    ArrayList<User> users = new ArrayList<>();

    void initUI() {
        barChart = findViewById(R.id.barChart);
    }

    private int getMonthIndex(String month) {
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int monthIndex = Integer.parseInt(parts[1]) - 1;
        return year * 12 + monthIndex;
    }

    void onDataLoaded() {
        ArrayList<BarEntry> entries = new ArrayList<>();


        HashMap<String, Integer> countByMonth = new HashMap<>();
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


        ArrayList<String> labels = new ArrayList<>();
        for (String key : countByMonth.keySet()) {
            int count = countByMonth.get(key);
            labels.add(key + "month");
            entries.add(new BarEntry(Float.parseFloat(key), count));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Number of Users Created by Month");
        dataSet.setValueTextSize(16f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        BarData data = new BarData(dataSet);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.setData(data);


    }

    void initData() {
        database = FirebaseFirestore.getInstance();
        database.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            User u = d.toObject(User.class);
                            users.add(u);
                        }
                        onDataLoaded();
                    }
                });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        initUI();
        initData();
    }
}