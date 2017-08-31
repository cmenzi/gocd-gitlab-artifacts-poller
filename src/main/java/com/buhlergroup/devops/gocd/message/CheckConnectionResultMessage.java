package com.buhlergroup.devops.gocd.message;

import java.util.List;

import com.google.gson.annotations.Expose;

public class CheckConnectionResultMessage {

    public enum STATUS {SUCCESS, FAILURE}

    @Expose
    private STATUS status;

    @Expose
    private List<String> messages;

    public CheckConnectionResultMessage(STATUS status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public boolean success() {
        return STATUS.SUCCESS.equals(status);
    }

    public List<String> getMessages() {
        return messages;
    }
}
