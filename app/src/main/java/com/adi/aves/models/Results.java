
package com.adi.aves.models;

import java.util.List;

public class Results {
    private String plate;
    private List<Candidate> candidates;
    private Double score;

    @Override
    public String toString() {
        return "Results{" +
                "plate='" + plate + '\'' +
                ", candidates=" + candidates +
                ", score=" + score +
                '}';
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

}
