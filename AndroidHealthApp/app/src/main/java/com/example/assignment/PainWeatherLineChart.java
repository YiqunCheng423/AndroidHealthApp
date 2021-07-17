package com.example.assignment;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment.databinding.PainWeatherLineChartBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PainWeatherLineChart extends Fragment {
    private PainWeatherLineChartBinding binding;
    private PainRecordViewModel painRecordViewModel;
    private FirebaseAuth authentication;
    private List<PainRecordTable> painRecords = new ArrayList<>();
    private static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";
    private SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);
    private String start, end;

    public PainWeatherLineChart() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        binding = PainWeatherLineChartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PainRecordViewModel.class);
        binding.startDateButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    start = year + "-" + (month + 1) + "-" + dayOfMonth;

                    binding.startDateTextView.setText(start);
                }
            }, year, month, day);

            datePickerDialog.show();
        });

        // set pop up date picker
        binding.endDateButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    end = year + "-" + (month + 1) + "-" + dayOfMonth;
                    binding.endDateTextView.setText(end);
                }
            }, year, month, day);

            datePickerDialog.show();
        });

        // List weather info (temperature, humidity, pressure)
        List<String> weatherChoice = new ArrayList<>();
        weatherChoice.add("temperature");
        weatherChoice.add("humidity");
        weatherChoice.add("pressure");

        // set weather info to spinner
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, weatherChoice);
        binding.weatherChoiceSpinner.setAdapter(spinnerAdapter);

        initChart();
        authentication = FirebaseAuth.getInstance();
        String email = authentication.getCurrentUser().getEmail();

        // comfirm button and get the line chart
        binding.confirmButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
                painRecordViewModel.getAllDailyRecordsByEmail(email).thenApply(v1 -> {
                    painRecords = v1;
                    setData();
                    updateY();
                    binding.chart.invalidate();
                    return v1;
                });
            } else {
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = sf.parse(start);
                    endDate = sf.parse(end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                painRecordViewModel.getDailyRecordsByEmail(email, startDate.getTime(), endDate.getTime()).thenApply(v1 -> {
                    painRecords = v1;
                    setData();
                    updateY();
                    binding.chart.invalidate();
                    return v1;
                });
            }

        });

        // correlation data
        binding.correlationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = binding.weatherChoiceSpinner.getSelectedItem().toString();
                Intent intent = new Intent(getActivity(), CorrelationActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("start", start);
                intent.putExtra("end", end);
                startActivity(intent);
            }
        });
        return view;
    }

    private void updateY() {
        YAxis rightAxis = binding.chart.getAxisRight();
        if (binding.weatherChoiceSpinner.getSelectedItem().toString().equals("temperature")) {
            rightAxis.setAxisMaximum(30);
            rightAxis.setAxisMinimum(0);
        } else if (binding.weatherChoiceSpinner.getSelectedItem().toString().equals("humidity")) {
            rightAxis.setAxisMaximum(100);
            rightAxis.setAxisMinimum(0);
        } else if (binding.weatherChoiceSpinner.getSelectedItem().toString().equals("pressure")) {
            rightAxis.setAxisMaximum(1050);
            rightAxis.setAxisMinimum(1000);
        }

    }

    private void initChart() {
        // no description text
        binding.chart.getDescription().setEnabled(false);

        // enable touch gestures
        binding.chart.setTouchEnabled(true);

        binding.chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        binding.chart.setDragEnabled(true);
        binding.chart.setScaleEnabled(true);
        binding.chart.setDrawGridBackground(false);
        binding.chart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chart.setPinchZoom(true);

        // set an alternative background color
        binding.chart.setBackgroundColor(Color.WHITE);

        binding.chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = binding.chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(tfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = binding.chart.getXAxis();
//        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String date = sf.format(new Date(painRecords.get((int) value).getDate()));
                return date;
            }
        });
        YAxis leftAxis = binding.chart.getAxisRight();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(10);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = binding.chart.getAxisRight();
//        rightAxis.setTypeface(tfLight);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaximum(100);
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }

    private void setData() {
        ArrayList<Entry> values1 = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();
        for (int i = 0; i < painRecords.size(); i++) {
            values1.add(new Entry(i, painRecords.get(i).getPainLevel()));
            if (binding.weatherChoiceSpinner.getSelectedItem().toString().equals("temperature")) {
                values2.add(new Entry(i, painRecords.get(i).getTemperature()));
            } else if (binding.weatherChoiceSpinner.getSelectedItem().toString().equals("humidity")) {
                values2.add(new Entry(i, painRecords.get(i).getHumidity()));
            } else if (binding.weatherChoiceSpinner.getSelectedItem().toString().equals("pressure")) {
                values2.add(new Entry(i, painRecords.get(i).getPressure()));
            }

        }

        LineDataSet set1, set2;

        // create a dataset and give it a type
        set1 = new LineDataSet(values1, "Pain Level");

        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        //set1.setFillFormatter(new MyFillFormatter(0f));
        //set1.setDrawHorizontalHighlightIndicator(false);
        //set1.setVisible(false);
        //set1.setCircleHoleColor(Color.WHITE);

        // create a dataset and give it a type
        String title = binding.weatherChoiceSpinner.getSelectedItem().toString();
        set2 = new LineDataSet(values2, title);
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.BLACK);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));

        // create a data object with the data sets
        LineData data = new LineData(set1, set2);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        // set data
        binding.chart.setData(data);

//        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
