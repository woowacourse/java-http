package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse response) throws IOException;
}
