package com.export.table.csv;

import java.time.LocalDateTime;

public class ExportResponse {

    private String jobId;
    private String message;
    private LocalDateTime timestamp;

    public ExportResponse(String jobId, String message) {
        this.jobId = jobId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
