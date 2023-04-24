package com.adi.aves.models;

import java.util.List;

public class PRResponse {
    private Object camera_id;
    private String filename;
    private Double processing_time;
    private List<Results> results;
    private String timestamp;
    private Long version;

    private String type;
    private String entry_id;
    private String number_plate;

    private String imageUrl;

    public static final String TYPE_IN = "IN";
    public static final String TYPE_OUT = "OUT";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(String entry_id) {
        this.entry_id = entry_id;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    @Override
    public String toString() {
        return "PRResponse{" +
                "camera_id=" + camera_id +
                ", filename='" + filename + '\'' +
                ", processing_time=" + processing_time +
                ", results=" + results +
                ", timestamp='" + timestamp + '\'' +
                ", version=" + version +
                '}';
    }

    public Object getCameraId() {
        return camera_id;
    }

    public void setCameraId(Object camera_id) {
        this.camera_id = camera_id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Double getProcessingTime() {
        return processing_time;
    }

    public void setProcessingTime(Double processingTime) {
        this.processing_time = processingTime;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
