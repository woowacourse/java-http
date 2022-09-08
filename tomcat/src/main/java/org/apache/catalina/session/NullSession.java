package org.apache.catalina.session;

import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class NullSession extends Session {

    public NullSession() {
        super(null);
    }

    @Override
    public boolean isExpired() {
        return true;
    }

    @Override
    public String getId() {
        throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean hasAttribute(String key) {
        return false;
    }

    @Override
    public void setAttribute(String key, Object value) {
        throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
