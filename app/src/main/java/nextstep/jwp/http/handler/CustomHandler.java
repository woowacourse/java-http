package nextstep.jwp.http.handler;

import nextstep.jwp.http.http_request.JwpHttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CustomHandler {

    String handle(JwpHttpRequest jwpHttpRequest) throws URISyntaxException, IOException;
}
