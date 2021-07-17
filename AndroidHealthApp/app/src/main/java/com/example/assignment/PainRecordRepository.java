package com.example.assignment;


import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PainRecordRepository {
    private PainRecordDAO painRecordDao;
    private LiveData<List<PainRecordTable>> allPainRecords;
    public PainRecordRepository(Application application){
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        painRecordDao =db.painRecordDao();
        allPainRecords= painRecordDao.getAll();
    }
    // Room executes this query on a separate thread
    public LiveData<List<PainRecordTable>> getAllPainRecords() {
        return allPainRecords;
    }
    public  void insert(final PainRecordTable painRecord){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.insert(painRecord);
            }
        });
    }
    public void deleteAll(){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.deleteAll();
            }
        });
    }
    public void delete(final PainRecordTable painRecord){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.delete(painRecord);
            }
        });
    }
    public void updatePainRecord(final PainRecordTable painRecord){
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.updatePainRecordDao(painRecord);
            }
        });
    }

    // Daily Record
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecordTable>> getAllDailyRecords() {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecordTable>>() {
            @Override
            public List<PainRecordTable> get() {
                return painRecordDao.getAllDailyRecords();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecordTable> getCurrentDailyRecords() {
        return CompletableFuture.supplyAsync(new Supplier<PainRecordTable>() {
            @Override
            public PainRecordTable get() {
                return painRecordDao.getCurrentRecord();
            }
        });
    }

    // Count pain location to get pie chart
    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainLocation>> getAllPainLocations(String email) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainLocation>>() {
            @Override
            public List<PainLocation> get() {
                return painRecordDao.countPainLocation(email);
            }
        });
    }

    public void deleteById(int painRecordID) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.deleteById(painRecordID);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecordTable>> getAllDailyRecordsByEmail(String email) {
            return CompletableFuture.supplyAsync(new Supplier<List<PainRecordTable>>() {
                @Override
                public List<PainRecordTable> get() {
                    return painRecordDao.getAllDailyRecordsByEmail(email);
                }
            });
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecordTable>> getDailyRecordsByEmail(String email, long start, long end) {
        return CompletableFuture.supplyAsync(new Supplier<List<PainRecordTable>>() {
            @Override
            public List<PainRecordTable> get() {
                return painRecordDao.getDailyRecordsByEmail(email,start,end);
            }
        });
    }
}
