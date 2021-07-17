package com.example.assignment;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordViewModel extends AndroidViewModel {
    private PainRecordRepository cRepository;
    private LiveData<List<PainRecordTable>> allPainRecords;
    public PainRecordViewModel (Application application) {
        super(application);
        cRepository = new PainRecordRepository(application);
        allPainRecords = cRepository.getAllPainRecords();
    }

    // Daily Record
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecordTable>> getAllDailyRecords(){
        return cRepository.getAllDailyRecords();
    }

    // get all records of current users signed in
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecordTable>> getAllDailyRecordsByEmail(String email){
        return cRepository.getAllDailyRecordsByEmail(email);
    }

    // get all records of current users signed in in selected date
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecordTable>> getDailyRecordsByEmail(String email,long start,long end){
        return cRepository.getDailyRecordsByEmail(email,start,end);
    }

    // current day records
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecordTable> getCurrentDailyRecords(){
        return cRepository.getCurrentDailyRecords();
    }

    // Count pain location to get pie chart
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainLocation>> getAllPainLocations(String email){
        return cRepository.getAllPainLocations(email);
    }

    public LiveData<List<PainRecordTable>> getAllPainRecords() {
        return allPainRecords;
    }

    // insert the record
    public void insert(PainRecordTable painRecord) {
        cRepository.insert(painRecord);
    }

    public void deleteAll() {
        cRepository.deleteAll();
    }

    // update the record
    public void update(PainRecordTable painRecord) {
        cRepository.updatePainRecord(painRecord);
    }

    public void deleteById(int painRecordID) {
        cRepository.deleteById(painRecordID);
    }
}

