package com.example.assignment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.assignment.databinding.ActivityStepBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


public class StepActivity extends AppCompatActivity {

    private ActivityStepBinding binding;
    private PainRecordViewModel painRecordViewModel;
    public static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";
    private SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStepBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Step");
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StepActivity.this.finish();
            }
        });
        AnyChartView anyChartView = binding.anyChartView;
        anyChartView.setProgressBar(binding.progressBar);
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(PainRecordViewModel.class);

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(StepActivity.this, event.getData().get("x") + ":" + event.getData().get("value"),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // get data of pain location from database to create donut chart
        painRecordViewModel.getCurrentDailyRecords().thenApply(model -> {
            int totalStep=model.getSteps();
            int takenStep=model.getTakenSteps();
            List<DataEntry> data = new ArrayList<>();
            data.add(new ValueDataEntry("steps remaining", totalStep - takenStep));
            data.add(new ValueDataEntry("steps taken", takenStep));
            pie.data(data);

            pie.title("Step chart "+sf.format(new Date(model.getDate())) );

            pie.labels().position("outside");

            pie.legend().title().enabled(true);

            pie.legend().title()
                    .text(" ")
                    .padding(0d, 0d, 10d, 0d);

            pie.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER);
            pie.innerRadius("50%");
            return model;
        });

        anyChartView.setChart(pie);
    }

}