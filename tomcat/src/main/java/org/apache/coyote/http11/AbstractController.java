package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        } else if (httpMethod == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }
}
