package com.example.assignment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.CircularGauge;
import com.anychart.charts.Pie;
import com.anychart.core.axes.Circular;
import com.anychart.core.gauge.pointers.Bar;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.LegendLayout;
import com.anychart.graphics.vector.Fill;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;
import com.anychart.graphics.vector.text.VAlign;
import com.example.assignment.databinding.ReportsFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class ReportsFragment extends Fragment {

    private ReportsFragmentBinding binding;
    private PainRecordViewModel painRecordViewModel;
    private FirebaseAuth authentication;

    public ReportsFragment(){}
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        binding = ReportsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        AnyChartView anyChartView = binding.anyChartView;
        anyChartView.setProgressBar(binding.progressBar);
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);

        binding.buttonStepPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),
                        StepActivity.class);
                startActivity(intent);
            }
        });

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(getActivity(), event.getData().get("x") + ":" + event.getData().get("value"),
                        Toast.LENGTH_SHORT).show();
            }
        });
        authentication = FirebaseAuth.getInstance();
        String email = authentication.getCurrentUser().getEmail();
        // get data of pain location from database
        painRecordViewModel.getAllPainLocations(email).thenApply(model -> {
            List<DataEntry> data = new ArrayList<>();
            // for loop
            model.forEach(v -> {
                data.add(new ValueDataEntry(v.getPainLocation(), v.getTotal()));
            });

            pie.data(data);
            pie.title("Pain location");
            pie.labels().position("outside");
            pie.legend().title().enabled(true);
            pie.legend().title()
                    .text("Pie chart")
                    .padding(0d, 0d, 10d, 0d);
            pie.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER);
            return model;
        });

/*        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Apples", 6371664));
        data.add(new ValueDataEntry("Pears", 789622));
        data.add(new ValueDataEntry("Bananas", 7216301));
        data.add(new ValueDataEntry("Grapes", 1486621));
        data.add(new ValueDataEntry("Oranges", 1200000));

        pie.data(data);

        pie.title("Pain location");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Retail channels")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);*/

        anyChartView.setChart(pie);

        return view;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}