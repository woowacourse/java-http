package org.apache.coyote.http11.url;

import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class Register extends Url {
    public Register(String url) {
        super(url);
    }

    @Override
    public Http11Response getResource() {
        return new Http11Response(ContentType.from(getPath()), HttpStatus.OK, getPath());
    }
}
