package org.apache.coyote.http11.request;

import java.io.InputStream;

import org.apache.commons.lang3.NotImplementedException;

public class Request {

    public Request(InputStream inputStream) {
    }

    public Method getMethod() {
        throw new NotImplementedException();
    }

    public String getTarget() {
        throw new NotImplementedException();
    }
}
