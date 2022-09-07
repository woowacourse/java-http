package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;

public interface Controller {

    boolean isUrlMatches(final String url);

    HttpResponse doGet(final HttpRequest request) throws IOException;

    HttpResponse doPost(final HttpRequest request) throws IOException;
}
