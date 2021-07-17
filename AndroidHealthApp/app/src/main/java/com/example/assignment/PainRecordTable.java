package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
// set the database table
@Entity
public class PainRecordTable {

    @PrimaryKey(autoGenerate = true)
    public int painRecordID;
    @ColumnInfo(name = "email")
    @NonNull
    private String email;
    @ColumnInfo(name = "pain_level")
    @NonNull
    private int painLevel;
    @ColumnInfo(name = "pain_location")
    @NonNull
    private String painLocation;
    @ColumnInfo(name = "mood_rate")
    @NonNull
    private String moodRate;
    @ColumnInfo(name = "steps")
    @NonNull
    private int steps;
    @ColumnInfo(name = "taken_steps")
    @NonNull
    private int takenSteps;
    @ColumnInfo(name = "date")
    @NonNull
    private long date;
    @ColumnInfo(name = "temperature")
    @NonNull
    private int temperature;
    @ColumnInfo(name = "humidity")
    @NonNull
    private int humidity;
    @ColumnInfo(name = "pressure")
    @NonNull
    private int pressure;

    public PainRecordTable(@NonNull String email, int painLevel, @NonNull String painLocation, @NonNull String moodRate, int steps,int takenSteps, @NonNull long date, int temperature, int humidity, int pressure) {
        this.email = email;
        this.painLevel = painLevel;
        this.painLocation = painLocation;
        this.moodRate = moodRate;
        this.steps = steps;
        this.takenSteps=takenSteps;
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public int getPainRecordID() {
        return painRecordID;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public int getPainLevel() {
        return painLevel;
    }

    public void setPainLevel(int painLevel) {
        this.painLevel = painLevel;
    }

    @NonNull
    public String getPainLocation() {
        return painLocation;
    }

    public void setPainLocation(@NonNull String painLocation) {
        this.painLocation = painLocation;
    }

    @NonNull
    public String getMoodRate() {
        return moodRate;
    }

    public void setMoodRate(@NonNull String moodRate) {
        this.moodRate = moodRate;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @NonNull
    public long getDate() {
        return date;
    }

    public void setDate(@NonNull long date) {
        this.date = date;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setPainRecordID(int painRecordID) {
        this.painRecordID = painRecordID;
    }

    public int getTakenSteps() {
        return takenSteps;
    }

    public void setTakenSteps(int takenSteps) {
        this.takenSteps = takenSteps;
    }

    @Override
    public String toString() {
        return "PainRecordTable{" +
                "painRecordID=" + painRecordID +
                ", email='" + email + '\'' +
                ", painLevel=" + painLevel +
                ", painLocation='" + painLocation + '\'' +
                ", moodRate='" + moodRate + '\'' +
                ", steps=" + steps +
                ", takenSteps=" + takenSteps +
                ", date='" + date + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                '}';
    }
}
