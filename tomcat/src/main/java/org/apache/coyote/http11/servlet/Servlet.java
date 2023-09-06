package org.apache.coyote.http11.servlet;

import java.io.IOException;
import org.apache.coyote.http11.common.request.HttpRequest;
import org.apache.coyote.http11.common.response.HttpResponse;

public interface Servlet {

    HttpResponse handle(HttpRequest request) throws IOException;
}
