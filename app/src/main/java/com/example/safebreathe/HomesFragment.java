package com.example.safebreathe;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineDataSet;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.jjoe64.graphview.DefaultLabelFormatter;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;
//
//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;


public class HomesFragment extends Fragment {
    private TextView currentAQITextView;
    private ImageView currentAQIImageView;
    String status;
    int intStatus;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Calendar calendar = Calendar.getInstance();
    DatabaseReference databaseReference;
    LineChart lineChart;
    LineDataSet lineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;
    CircleProgress circleProgress;
    Spinner spinner;
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference;


    public HomesFragment() {
        // require a empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.graph);
        circleProgress = view.findViewById(R.id.circle_progress_aqi);
        currentAQITextView = view.findViewById(R.id.current_air_quality_textView);
        currentAQIImageView = view.findViewById(R.id.current_air_quality_ImageView);
        spinner = view.findViewById(R.id.drop_down_impact);

        String[] arraySpinner = new String[] {"Impact", "Precautions"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity().getApplicationContext(), MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, 0);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("mq135");
        retrieveData();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                intStatus = Integer.parseInt(dataSnapshot.child("mq135").getValue().toString());
//                mq135.setText(status);
                circleProgress.setProgress(Integer.parseInt(dataSnapshot.child("mq135").getValue().toString()));
                if(intStatus >=0 && intStatus <=50) {
                    circleProgress.setFinishedColor(Color.parseColor("#228B22"));
                    currentAQITextView.setText("GOOD");
                    currentAQITextView.setBackgroundColor(Color.parseColor("#228B22"));
                    currentAQIImageView.setImageDrawable(getResources().getDrawable(R.drawable.good));
                } else if(intStatus >50 && intStatus <=100) {
                    circleProgress.setFinishedColor(Color.parseColor("#9ACD32"));
                    currentAQITextView.setText("SATISFACTORY");
                    currentAQITextView.setBackgroundColor(Color.parseColor("#9ACD32"));
                    currentAQIImageView.setImageDrawable(getResources().getDrawable(R.drawable.satisfactory));
                } else if(intStatus >100 && intStatus <=200) {
                    circleProgress.setFinishedColor(Color.parseColor("#FFFF00"));
                    currentAQITextView.setText("MODERATE");
                    currentAQITextView.setBackgroundColor(Color.parseColor("#FFFF00"));
                    currentAQIImageView.setImageDrawable(getResources().getDrawable(R.drawable.moderate));
                } else if(intStatus >200 && intStatus <=300) {
                    circleProgress.setFinishedColor(Color.parseColor("#E1AD01"));
                    currentAQITextView.setText("POOR");
                    currentAQITextView.setBackgroundColor(Color.parseColor("#E1AD01"));
                    currentAQIImageView.setImageDrawable(getResources().getDrawable(R.drawable.poor));
                } else if(intStatus >300 && intStatus <=400) {
                    circleProgress.setFinishedColor(Color.parseColor("#FF0000"));
                    currentAQITextView.setText("VERY POOR");
                    currentAQITextView.setBackgroundColor(Color.parseColor("#FF0000"));
                    currentAQIImageView.setImageDrawable(getResources().getDrawable(R.drawable.vpoor));
                } else {
                    circleProgress.setFinishedColor(Color.parseColor("#8E1600"));
                    currentAQITextView.setText("SEVERE");
                    currentAQITextView.setBackgroundColor(Color.parseColor("#8E1600"));
                    currentAQIImageView.setImageDrawable(getResources().getDrawable(R.drawable.severe));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status = snapshot.child("mq135").getValue().toString();
                double statusDouble = Double.parseDouble(status);
                if(statusDouble>300) {

                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+919053466227", null, "Alert! Air not safe. AQI:" + status, null, null);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void retrieveData() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> dataVals = new ArrayList<Entry>();
                if(snapshot.hasChildren()) {
                    for(DataSnapshot myDataSnapshot : snapshot.getChildren()) {
                        dataVals.add(new Entry( calendar.getTimeInMillis(), Integer.parseInt(snapshot.child("mq135").getValue().toString())));
                    }
                    showChart(dataVals);

                } else {
                    lineChart.clear();
                    lineChart.invalidate();
                }

            }

            private void showChart(ArrayList<Entry> dataVals) {
                lineDataSet.setValues(dataVals);
                lineDataSet.setLabel("Air Quality Index");
                iLineDataSets.clear();
                iLineDataSets.add(lineDataSet);
                lineData = new LineData(iLineDataSets);
                YAxis rightYAxis = lineChart.getAxisRight();
                rightYAxis.setEnabled(false);
                lineChart.getXAxis().setEnabled(true);
                lineChart.clear();
                lineChart.setData(lineData);
                lineChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

