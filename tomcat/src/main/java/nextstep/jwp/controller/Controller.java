package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    HttpResponse service(final HttpRequest httpRequest) throws IOException;

    boolean canHandle(final HttpRequestURI httpRequestURI);
}
