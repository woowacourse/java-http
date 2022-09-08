package org.apache.catalina.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        if (isGetRequest(request)) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected abstract HttpResponse doGet(final HttpRequest request);

    protected abstract HttpResponse doPost(final HttpRequest request);

    protected boolean isGetRequest(final HttpRequest request) {
        return request.isGetRequest();
    }

    protected String getStaticResource(final URL url) {
        try {
            return Files.readString(new File(Objects.requireNonNull(url)
                .getFile())
                .toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("No such resource");
        }
    }

}
