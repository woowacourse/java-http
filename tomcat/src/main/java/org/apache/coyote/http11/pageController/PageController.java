package org.apache.coyote.http11.pageController;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface PageController {
    HttpResponse run(HttpRequest httpRequest) throws IOException;
}
