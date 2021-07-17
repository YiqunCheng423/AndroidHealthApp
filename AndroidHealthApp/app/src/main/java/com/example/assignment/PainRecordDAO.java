package com.example.assignment;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
// Database query
@Dao
public interface PainRecordDAO {
    @Query("SELECT * FROM PainRecordTable ORDER BY date DESC")
    LiveData<List<PainRecordTable>> getAll();
    @Query("SELECT * FROM PainRecordTable WHERE painRecordID = :pain_record_id LIMIT 1")
    PainRecordTable findByID(int pain_record_id);
    @Query("SELECT * FROM PainRecordTable ORDER BY painRecordID DESC LIMIT 1")
    PainRecordTable getCurrentRecord();
    @Insert
    void insert(PainRecordTable painRecordTable);
    @Delete
    void delete(PainRecordTable painRecordTable);
    @Update
    void updatePainRecordDao(PainRecordTable painRecordTable);
    @Query("DELETE FROM PainRecordTable")
    void deleteAll();
    @Query("DELETE FROM PainRecordTable WHERE painRecordID = :pain_record_id")
    void deleteById(int pain_record_id);
    @Query("SELECT * FROM PainRecordTable ORDER BY painRecordID ASC")
    List<PainRecordTable> getAllDailyRecords();
    // where email = :email
    @Query("select COUNT(pain_location) as total, pain_location as painLocation from painRecordTable WHERE email = :email group by pain_location")
    List<PainLocation> countPainLocation(String email);

    @Query("SELECT * FROM PainRecordTable WHERE email = :email ORDER BY date DESC")
    List<PainRecordTable> getAllDailyRecordsByEmail(String email);

    @Query("SELECT * FROM PainRecordTable WHERE email = :email AND date >= :start and date <= :end ORDER BY date DESC")
    List<PainRecordTable> getDailyRecordsByEmail(String email,long start,long end);
}
