package org.apache.catalina;

import java.io.IOException;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isSameMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException("지원하지 않는 Http Method 입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }
}
