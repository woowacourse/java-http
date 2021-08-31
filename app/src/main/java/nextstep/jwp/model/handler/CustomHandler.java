package nextstep.jwp.model.handler;

import nextstep.jwp.model.http_request.JwpHttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;

public interface CustomHandler {

    String handle(JwpHttpRequest jwpHttpRequest) throws URISyntaxException, IOException;
}
