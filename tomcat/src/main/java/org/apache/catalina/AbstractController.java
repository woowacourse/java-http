package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        if (request.equalMethod(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.equalMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        byte[] body = Files.readAllBytes(getStaticResource("/405.html"));
        return HttpResponse.methodNotAllowed()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        byte[] body = Files.readAllBytes(getStaticResource("/405.html"));
        return HttpResponse.methodNotAllowed()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        byte[] body = Files.readAllBytes(getStaticResource("/405.html"));
        return HttpResponse.methodNotAllowed()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(body.length)
                .body(body)
                .build();
    }

    protected Path getStaticResource(String url) {
        URL resourceURL = getClass().getClassLoader().getResource("static" + url);
        if (resourceURL == null) {
            return null;
        }
        return Path.of(resourceURL.getFile());
    }
}
