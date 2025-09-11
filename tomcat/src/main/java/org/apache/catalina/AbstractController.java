package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        if (request.equalMethod(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.equalMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        byte[] body = getStaticResource("/405.html");
        return HttpResponse.methodNotAllowed()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        byte[] body = getStaticResource("/405.html");
        return HttpResponse.methodNotAllowed()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        byte[] body = getStaticResource("/405.html");
        return HttpResponse.methodNotAllowed()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    protected byte[] getStaticResource(String url) throws IOException {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("static" + url)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        }
    }
}
