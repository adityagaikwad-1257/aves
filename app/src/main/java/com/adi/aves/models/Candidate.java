package com.adi.aves.models;

public class Candidate {
    private String plate;
    private Double score;

    @Override
    public String toString() {
        return "Candidate{" +
                "plate='" + plate + '\'' +
                ", score=" + score +
                '}';
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

}
