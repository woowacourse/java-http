package org.apache.coyote.http11.pageController;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface PageController {
    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
