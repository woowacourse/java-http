package org.apache.coyote.handle.handler;

import java.io.IOException;
import org.apache.coyote.handle.ViewResolver;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Handler {

    ViewResolver viewResolver = ViewResolver.getInstance();

    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;

    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;
}
