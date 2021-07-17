package com.example.assignment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import org.apache.commons.math3.linear.MatrixUtils;
//import org.apache.commons.math3.linear.RealMatrix;
//import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import com.example.assignment.databinding.ActivityCorrelationBinding;
import com.example.assignment.databinding.ActivityStepBinding;
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
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;


public class CorrelationActivity extends AppCompatActivity {

    private ActivityCorrelationBinding binding;
    private PainRecordViewModel painRecordViewModel;
    public static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";
    private SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);
    private FirebaseAuth authentication;
    private List<PainRecordTable> painRecords;
    private String type, start, end;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCorrelationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(PainRecordViewModel.class);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Test Correlation");
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CorrelationActivity.this.finish();
            }
        });
        initChart();
        authentication = FirebaseAuth.getInstance();
        String email = authentication.getCurrentUser().getEmail();

        type = getIntent().getStringExtra("type");
        start = getIntent().getStringExtra("start");
        end = getIntent().getStringExtra("end");
        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            painRecordViewModel.getAllDailyRecordsByEmail(email).thenApply(v1 -> {
                painRecords = v1;
                setData();
                updateY();
                binding.chart.invalidate();
                binding.correlationText.setText(testCorrelation());
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
                binding.correlationText.setText(testCorrelation());
                updateY();
                binding.chart.invalidate();
                return v1;
            });
        }
    }

    // update Y axis, set a new Y ais at right
    private void updateY() {
        YAxis rightAxis = binding.chart.getAxisRight();
        if (type.equals("temperature")) {
            rightAxis.setAxisMaximum(40);
            rightAxis.setAxisMinimum(0);
        } else if (type.equals("humidity")) {
            rightAxis.setAxisMaximum(150);
            rightAxis.setAxisMinimum(0);
        } else if (type.equals("pressure")) {
            rightAxis.setAxisMaximum(3000);
            rightAxis.setAxisMinimum(500);
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

    // get data choice of weather
    private void setData() {
        ArrayList<Entry> values1 = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();
        for (int i = 0; i < painRecords.size(); i++) {
            values1.add(new Entry(i, painRecords.get(i).getPainLevel()));
            if (type.equals("temperature")) {
                values2.add(new Entry(i, painRecords.get(i).getTemperature()));
            } else if (type.equals("humidity")) {
                values2.add(new Entry(i, painRecords.get(i).getHumidity()));
            } else if (type.equals("pressure")) {
                values2.add(new Entry(i, painRecords.get(i).getPressure()));
            }

        }

        LineDataSet set1, set2;

//        if (binding.chart.getData() != null &&
//                binding.chart.getData().getDataSetCount() > 0) {
//            set1 = (LineDataSet) binding.chart.getData().getDataSetByIndex(0);
//            set2 = (LineDataSet) binding.chart.getData().getDataSetByIndex(1);
//            set1.setValues(values1);
//            set2.setValues(values2);
//            binding.chart.getData().notifyDataChanged();
//            binding.chart.notifyDataSetChanged();
//        } else {
        // create a data set and give it a type
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
        set2 = new LineDataSet(values2, type);
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

    public String testCorrelation(){
        // two column array: 1st column=first array, 1st column=second array
        double data[][] =new double[painRecords.size()][];
        for (int i = 0; i < painRecords.size(); i++) {
            double data1[]=new double[2];
            data1[0]=painRecords.get(i).getPainLevel();
            if (type.equals("temperature")) {
                data1[1]=painRecords.get(i).getTemperature();
            } else if (type.equals("humidity")) {
                data1[1]=painRecords.get(i).getHumidity();
            } else if (type.equals("pressure")) {
                data1[1]=painRecords.get(i).getPressure();
            }
            data[i]=data1;
        }

        // create a real matrix
        RealMatrix m = MatrixUtils.createRealMatrix(data);
        // measure all correlation test: x-x, x-y, y-x, y-x
        for (int i = 0; i < m.getColumnDimension(); i++)
            for (int j = 0; j < m.getColumnDimension(); j++) {
                PearsonsCorrelation pc = new PearsonsCorrelation();
                double cor = pc.correlation(m.getColumn(i), m.getColumn(j));
                System.out.println(i + "," + j + "=[" + String.format(".%2f",
                        cor) + "," + "]");
            }
        // correlation test (another method): x-y
        PearsonsCorrelation pc = new PearsonsCorrelation(m);
        RealMatrix corM = pc.getCorrelationMatrix();
        // significant test of the correlation coefficient (p-value)
        RealMatrix pM = pc.getCorrelationPValues();
        return("p value:" + String.format("%.2f",
                pM.getEntry(0, 1)) + " correlation: " +
                String.format("%.2f", corM.getEntry(0, 1)));
    }
}