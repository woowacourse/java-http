package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if(HttpMethod.POST == request.getHttpMethod()) {
            doPost(request, response);
            return;
        }
        if(HttpMethod.GET == request.getHttpMethod()) {
            doGet(request, response);
            return;
        }
        throw new IllegalArgumentException("지원하지 않는 http 메서드입니다.");
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;
}
