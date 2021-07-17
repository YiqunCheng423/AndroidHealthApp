package com.example.assignment;

public class PainLocation {
    private String painLocation;
    private int total;

    public PainLocation(String painLocation, int total) {
        this.painLocation = painLocation;
        this.total = total;
    }

    public String getPainLocation() {
        return painLocation;
    }

    public void setPainLocation(String painLocation) {
        this.painLocation = painLocation;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
