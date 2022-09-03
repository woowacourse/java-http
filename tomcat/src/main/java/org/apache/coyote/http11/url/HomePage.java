package org.apache.coyote.http11.url;

import org.apache.coyote.http11.dto.Http11Request;

public class HomePage extends Url {

    public HomePage(final String url) {
        super(url);
    }

    @Override
    public Http11Request getRequest() {
        return new Http11Request(getPath());
    }
}
