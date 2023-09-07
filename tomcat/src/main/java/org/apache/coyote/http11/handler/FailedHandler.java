package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.Request;

public class FailedHandler extends Handler{
    @Override
    public void setNext(Handler handler) {
    }

    @Override
    public String getResponse(Request request) {
        throw new NoSuchApiException();
    }
}
