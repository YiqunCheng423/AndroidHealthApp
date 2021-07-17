package com.example.assignment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.databinding.DailyRecordFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smileyrating.SmileyRating;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.example.assignment.DataEntryFragment.PATTEN_DEFAULT_YMD;


public class DailyRecordFragment extends Fragment {
    private DailyRecordFragmentBinding binding;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<PainRecordTable> painRecords;
    private PainRecordViewModel painRecordViewModel;
    private FirebaseAuth authentication;
    private int[] levels = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private String[] locations = {"Back", "Neck", "Head", "Knees", "Hips", "Abdomen", "Elbows", "Shoulders", "Shins", "Jaw", "Facial"};
    private String[] rates = {"Very Good", "Good", "Average", "Low", "Very Low"};

    public DailyRecordFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the View for this fragment using the binding
        binding = DailyRecordFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        authentication = FirebaseAuth.getInstance();
        String email = authentication.getCurrentUser().getEmail();
        painRecords = new ArrayList<>();
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().
                getApplication()).create(PainRecordViewModel.class);
        // find all records in room database and store in list
        painRecordViewModel.getAllDailyRecordsByEmail(email).thenApply(v -> {
            painRecords = v;
            //add test data randomly
            if (painRecords.size() == 0) {
                for (int i = 7; i > 0; i--) {
                    Random random = new Random();
                    int r = random.nextInt(levels.length);
                    Random random1 = new Random();
                    int q = random1.nextInt(locations.length);
                    Random random2 = new Random();
                    int s = random2.nextInt(rates.length);
                    Random random3 = new Random();
                    int g = random3.nextInt(10000);
                    Random random4 = new Random();
                    int t = random4.nextInt(5000);
                    Random random5 = new Random();
                    int temperature =random5.nextInt(30);
                    Random random6 = new Random();
                    int humidity =30+ random6.nextInt(50);
                    Random random7 = new Random();
                    int pressure =1015+ random7.nextInt(5);
                    PainRecordTable painRecord = new PainRecordTable(email, levels[r], locations[q], rates[s],g,t, getDateOffset(i),temperature,humidity,pressure);
                    painRecordViewModel.insert(painRecord);
                }

            }
            return v;
        });

        adapter = new RecyclerViewAdapter(painRecords);

        //this just creates a line divider between rows
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(layoutManager);

        painRecordViewModel.getAllPainRecords().observe(getActivity(), new Observer<List<PainRecordTable>>() {
            @Override
            public void onChanged(List<PainRecordTable> painRecordTables) {
                adapter.setPainRecords(painRecords);
            }
        });

        return view;
    }

    // get date when generating test data
    private long getDateOffset(int offset) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -offset);
        calendar1.set(Calendar.HOUR_OF_DAY,0);
        calendar1.set(Calendar.MINUTE,0);
        calendar1.set(Calendar.SECOND,0);
        calendar1.set(Calendar.MILLISECOND,0);
        return calendar1.getTime().getTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}