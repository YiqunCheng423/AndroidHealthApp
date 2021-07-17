package com.example.assignment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.assignment.databinding.DataEntryFragmentBinding;
import com.example.assignment.weatherModel.Root;
import com.google.firebase.auth.FirebaseAuth;
import com.hsalf.smileyrating.SmileyRating;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DataEntryFragment extends Fragment {
    private DataEntryFragmentBinding binding;
    private PainRecordTable currentRecord = null;
    public static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";
    private SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);
    private String email;

    public DataEntryFragment() {
    }

    private AlarmManager reminder;
    private PendingIntent reminderIntent;
    private PainRecordViewModel painRecordViewModel;
    private FirebaseAuth authentication;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataEntryFragmentBinding.inflate(inflater, container, false);

        View view = binding.getRoot();
        SharedViewModel model = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        painRecordViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PainRecordViewModel.class);
        authentication = FirebaseAuth.getInstance();
        email = authentication.getCurrentUser().getEmail();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            painRecordViewModel.getAllDailyRecordsByEmail(email).thenApply(v -> {
                currentRecord = getTodayRecord(v);
                return v;
            });
        }
        // test
        painRecordViewModel.getAllPainRecords().observe(getActivity(), new Observer<List<PainRecordTable>>() {
            @Override
            public void onChanged(List<PainRecordTable> painRecordTables) {
                StringBuffer records = new StringBuffer();
                for (PainRecordTable painRecordTable : painRecordTables) {
                    String painRecordTableDetail = painRecordTable.getPainRecordID() + " " + painRecordTable.getEmail()
                            + " " + painRecordTable.getPainLevel() + " " + painRecordTable.getMoodRate() + " "
                            + painRecordTable.getDate() + painRecordTable.getTemperature();
                    records.append(System.getProperty("line.separator")).append(painRecordTableDetail);
                }
                System.out.println(records.toString());
            }
        });

        // List pain level
        List<Integer> painLevel = new ArrayList<>();
        painLevel.add(0);
        painLevel.add(1);
        painLevel.add(2);
        painLevel.add(3);
        painLevel.add(4);
        painLevel.add(5);
        painLevel.add(6);
        painLevel.add(7);
        painLevel.add(8);
        painLevel.add(9);
        painLevel.add(10);

        // set pain level to spinner
        final ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, painLevel);
        binding.painLevelSpinner.setAdapter(spinnerAdapter);

        // List pain location
        List<String> painLocation = new ArrayList<>();
        painLocation.add("Back");
        painLocation.add("Neck");
        painLocation.add("Head");
        painLocation.add("Knees");
        painLocation.add("Hips");
        painLocation.add("Abdomen");
        painLocation.add("Elbows");
        painLocation.add("Shoulders");
        painLocation.add("Shins");
        painLocation.add("Jaw");
        painLocation.add("Facial");

        // set pain location to spinner
        final ArrayAdapter<String> spinnerAdapterPainLocation = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, painLocation);
        binding.painLocationSpinner.setAdapter(spinnerAdapterPainLocation);

        // set mood conditions
        binding.moodRate.setTitle(SmileyRating.Type.GREAT, "Very Good");
        binding.moodRate.setTitle(SmileyRating.Type.GOOD, "Good");
        binding.moodRate.setTitle(SmileyRating.Type.OKAY, "Average");
        binding.moodRate.setTitle(SmileyRating.Type.BAD, "Low");
        binding.moodRate.setTitle(SmileyRating.Type.TERRIBLE, "Very Low");

        // edit steps and add default value as 10000
        binding.editSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String steps = binding.editSteps.getText().toString();
                if (!steps.isEmpty()) {
                    model.setMessage(steps);
                } else {
                    model.setMessage("10000");
                }
            }
        });

        // save button
        binding.saveButton.setOnClickListener(v -> {
            if (currentRecord != null) {
                Toast.makeText(getActivity(), "Record has been saved, Please update", Toast.LENGTH_SHORT).show();
                return;
            }
            // get user input
            int painLevelChosen = Integer.parseInt(binding.painLevelSpinner.getSelectedItem().toString());
            String painLocationChosen = binding.painLocationSpinner.getSelectedItem().toString();
            String tempMoodRate = binding.moodRate.getSelectedSmiley().toString();
            // get actual mood rate since using external code to create mood image for github

            String moodRateChosen = getSelectMoodRate(tempMoodRate);
            if (TextUtils.isEmpty(binding.editSteps.getText().toString())) {
                Toast.makeText(getActivity(), "Please enter your goal", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(binding.editTakenSteps.getText().toString())) {
                Toast.makeText(getActivity(), "Please enter your taken step", Toast.LENGTH_SHORT).show();
                return;
            }
            int takenSteps = Integer.parseInt(binding.editTakenSteps.getText().toString());
            if (TextUtils.isEmpty(moodRateChosen)) {
                Toast.makeText(getActivity(), "Please choose mood rate", Toast.LENGTH_SHORT).show();
                return;
            }
            // Store steps
            int steps = Integer.parseInt(binding.editSteps.getText().toString());

            // Store Email
            // Store Date
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            // Store weather
            // Generate and implement weather interface
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            // Create a class through Retrofit
            WeatherRetrofitAPI weatherInterface = retrofit.create(WeatherRetrofitAPI.class);
            // Call weather
            Call<Root> weather = weatherInterface.getWeather();
            // Get data
            weather.enqueue(new Callback<Root>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<Root> call, Response<Root> response) {
                    // Get root
                    Root root = response.body();
                    // Get temperature
                    double tempTemperature = root.getMain().getTemp();
                    // transfer
                    double temperature = tempTemperature - 273.15;
                    // humidity
                    int humidity = root.getMain().getHumidity();
                    // pressure
                    int pressure = root.getMain().getPressure();
                    PainRecordTable painRecord = new PainRecordTable(email, painLevelChosen, painLocationChosen,
                            moodRateChosen, steps,takenSteps, calendar.getTime().getTime(), (int) Math.round(temperature), humidity, pressure);
                    painRecordViewModel.insert(painRecord);
                    painRecordViewModel.getCurrentDailyRecords().thenApply(v -> {
                        currentRecord = v;
                        return v;
                    });
                    // disable all changes
                    binding.moodRate.disallowSelection(true);
                    binding.painLevelSpinner.setEnabled(false);
                    binding.painLocationSpinner.setEnabled(false);
                    binding.editSteps.setEnabled(false);
                    binding.editTakenSteps.setEnabled(false);
                    Toast.makeText(getActivity(), "save success", Toast.LENGTH_SHORT).show();
                    addAlarm();
                }

                @Override
                public void onFailure(Call<Root> call, Throwable t) {
                    System.out.print(t.getMessage());
                    Toast.makeText(getActivity(), "save success", Toast.LENGTH_SHORT).show();
                }
            });

        });

        //update button
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check error
                if (currentRecord == null) {
                    Toast.makeText(getActivity(), "Please save first", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.editSteps.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter your goal", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(binding.editTakenSteps.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter your taken step", Toast.LENGTH_SHORT).show();
                    return;
                }
                // If no errors, set the inputs
                int takenSteps = Integer.parseInt(binding.editTakenSteps.getText().toString());
                currentRecord.setTakenSteps(takenSteps);
                int steps = Integer.parseInt(binding.editSteps.getText().toString());
                currentRecord.setSteps(steps);
                int painLevelChosen = Integer.parseInt(binding.painLevelSpinner.getSelectedItem().toString());
                currentRecord.setPainLevel(painLevelChosen);
                String painLocationChosen = binding.painLocationSpinner.getSelectedItem().toString();
                currentRecord.setPainLocation(painLocationChosen);
                String tempMoodRate = binding.moodRate.getSelectedSmiley().toString();
                // get actual mood rate since using external code to create mood image for github
                String moodRateChosen = getSelectMoodRate(tempMoodRate);
                // Check error
                if (TextUtils.isEmpty(moodRateChosen)) {
                    Toast.makeText(getActivity(), "Please choose mood rate", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentRecord.setMoodRate(moodRateChosen);
//                painRecordViewModel.deleteById(currentRecord.getPainRecordID());
                painRecordViewModel.update(currentRecord);
//                addAlarm();
                Toast.makeText(getActivity(), "Update success", Toast.LENGTH_SHORT).show();
            }
        });

        // enable all changes
        binding.editButton.setOnClickListener(v -> {
            binding.moodRate.disallowSelection(false);
            binding.painLevelSpinner.setEnabled(true);
            binding.painLocationSpinner.setEnabled(true);
            binding.editSteps.setEnabled(true);
            binding.editTakenSteps.setEnabled(true);
        });


        return view;
    }

    // add reminder
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addAlarm() {
        // Set reminder
        reminder = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), ReminderActivity.class);
        intent.setAction("remind");

        reminderIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        int hour = binding.timePicker.getHour();
        int minute = binding.timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        reminder.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 1000 * 60 * 2, 0, reminderIntent);
    }

    // find whether is the current day
    private PainRecordTable getTodayRecord(List<PainRecordTable> lists) {
        for (int i = 0; i < lists.size(); i++) {
            long date = lists.get(i).getDate();
            String sd = sf.format(new Date(date));
            if (isToday(sd)) {
                return lists.get(i);
            }
        }
        return null;
    }

    // whether is current day
    private boolean isToday(String date) {
        Date now = new Date();
        String nowDay = sf.format(now);
        return date.equals(nowDay);
    }

    // transfer mood rate to the correct names
    private String getSelectMoodRate(String tempMoodRate) {
        String moodRate = "";
        switch (tempMoodRate) {
            case "TERRIBLE":
                moodRate = "Very Low";
                break;
            case "BAD":
                moodRate = "Low";
                break;
            case "OKAY":
                moodRate = "Average";
                break;
            case "GOOD":
                moodRate = "Good";
                break;
            case "GREAT":
                moodRate = "Very Good";
                break;
            default:
                moodRate = "";
                break;
        }
        return moodRate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
