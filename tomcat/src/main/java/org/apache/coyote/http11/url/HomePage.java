package org.apache.coyote.http11.url;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;

public class HomePage extends Url {

    public HomePage(final String url) {
        super(url);
    }

    @Override
    public Http11Response getResponse() {
        return new Http11Response(ContentType.from(getPath()), HttpStatus.OK, getPath());
    }
}
