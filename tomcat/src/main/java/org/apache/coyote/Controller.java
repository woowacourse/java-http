package org.apache.coyote;

import org.apache.coyote.http11.http.HttpRequest;

import java.io.IOException;

public abstract class Controller {

    public abstract String run(final HttpRequest request) throws IOException;

    public abstract String getResponse(final String uri) throws IOException;
}
