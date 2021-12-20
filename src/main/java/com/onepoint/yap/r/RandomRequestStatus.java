package com.onepoint.yap.r;

public record RandomRequestStatus(State state, String message) {

    public static RandomRequestStatus from(State state, String message) {
        RandomRequestStatus status = new RandomRequestStatus(state, message);
        return status;
    }

    public enum State {
        CREATED,
        PROCESSING,
        DONE,
        ERROR
    }
}
