package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Controller {

    void get(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;

    void post(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;
}
