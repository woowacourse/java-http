package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public interface Controller {
    void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException ;
}
