package com.onepoint.yap;

record RandomKillRequestStatus (State state, String message) {

    public static RandomKillRequestStatus from(State state, String message) {
        RandomKillRequestStatus status = new RandomKillRequestStatus(state, message);
        return status;
    }

    public enum State {
        CREATED,
        ALREADY_PRESENT,
        PROCESSING,
        DONE,
        ERROR,
        UNKNOWN
    }
}
