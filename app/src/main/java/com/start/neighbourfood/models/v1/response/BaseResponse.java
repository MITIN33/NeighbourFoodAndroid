package com.start.neighbourfood.models.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseResponse<T> {

    @JsonProperty("Version")
    private String Version;

    @JsonProperty("StatusCode")
    private int statusCode;

    @JsonProperty("RequestId")
    private String requestId;

    @JsonProperty("ErrorMessage")
    private String errorMessage;

    @JsonProperty("Result")
    private T result;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        this.Version = version;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
