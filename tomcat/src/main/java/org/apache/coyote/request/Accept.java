package org.apache.coyote.request;

public class Accept {

    private final String acceptType;

    private Accept(final String acceptType) {
        this.acceptType = acceptType;
    }

    public static Accept from(final String[] accepts) {
        return new Accept(accepts[0]);
    }

    public String getAcceptType() {
        return acceptType;
    }
}
