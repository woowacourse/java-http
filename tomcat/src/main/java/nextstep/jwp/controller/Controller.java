package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    void service(final HttpRequest request, final HttpResponse httpResponse) throws IOException;
}
