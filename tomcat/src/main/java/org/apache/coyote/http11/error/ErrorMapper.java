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
        if (e instanceof UnAuthorizedException) {
            return httpResourceLoader.load("401.html");
        }
        if (e instanceof NotFoundException) {
            return httpResourceLoader.load("404.html");
        }
        return httpResourceLoader.load("500.html");
    }
}
