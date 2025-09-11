package org.apache.coyote.http11.error;

import com.techcourse.exception.UnAuthorizedException;
import java.io.IOException;
import javassist.NotFoundException;
import org.apache.coyote.http11.HttpResourceLoader;
import org.apache.coyote.http11.HttpResponse;

public class ErrorMapper {

    private final HttpResourceLoader httpResourceLoader;

    public ErrorMapper(final HttpResourceLoader httpResourceLoader) {
        this.httpResourceLoader = httpResourceLoader;
    }

    public HttpResponse toHttpResponse(final Exception e) throws IOException {
        HttpResponse response = HttpResponse.create();
        findResource(e, response);
        return response;
    }

    private void findResource(final Exception e, final HttpResponse response) throws IOException {
        if (e instanceof UnAuthorizedException) {
            httpResourceLoader.load("401.html", response);
        }
        if (e instanceof NotFoundException) {
            httpResourceLoader.load("404.html", response);
        }

        httpResourceLoader.load("500.html", response);
    }
}
