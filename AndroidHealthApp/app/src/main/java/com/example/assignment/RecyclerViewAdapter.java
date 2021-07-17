package com.example.assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.databinding.RecyclerViewLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<PainRecordTable> painRecords;
    public static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";
    private SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);

    public RecyclerViewAdapter(List<PainRecordTable> painRecords) {
        this.painRecords = painRecords;
    }
    //This method creates a new view holder that is constructed with a new View, inflated from a layout
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewLayoutBinding binding = RecyclerViewLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder
                                         viewHolder, int position) {
        final PainRecordTable painRecord = painRecords.get(position);
        viewHolder.binding.recordId.setText("Record ID: "+Integer.toString(painRecord.getPainRecordID()));
        viewHolder.binding.recyclerViewPainLevel.setText("Pain Level: "+Integer.toString(painRecord.getPainLevel()));
        viewHolder.binding.recyclerViewPainLocation.setText("Pain Location: "+painRecord.getPainLocation());
        viewHolder.binding.recyclerViewMoodRate.setText("Mood Rate: "+painRecord.getMoodRate());
        viewHolder.binding.recyclerViewSteps.setText("Goal: "+(Integer.toString(painRecord.getSteps())));
        viewHolder.binding.recyclerTakenViewSteps.setText("Steps taken: "+(Integer.toString(painRecord.getTakenSteps())));
        String date = sf.format(new Date(painRecord.getDate()));
        viewHolder.binding.recyclerViewDate.setText("Date: "+date);
        viewHolder.binding.recyclerViewTemperature.setText("Temperature: "+(Integer.toString(painRecord.getTemperature())));
        viewHolder.binding.recyclerViewHumidity.setText("Humidity: "+(Integer.toString(painRecord.getHumidity())));
        viewHolder.binding.recyclerViewPressure.setText("Pressure: "+(Integer.toString(painRecord.getPressure())));
    }
    @Override
    public int getItemCount() {
        return painRecords.size();
    }

    public List<PainRecordTable> getPainRecords() {
        return painRecords;
    }

    public void setPainRecords(List<PainRecordTable> painRecords) {
        this.painRecords = painRecords;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerViewLayoutBinding binding;
        public ViewHolder(RecyclerViewLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
