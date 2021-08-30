package nextstep.jwp.model.handler;

import nextstep.jwp.model.http_request.JwpHttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

public interface CustomHandler {

    void handle(JwpHttpRequest uri, OutputStream outputStream) throws IOException, URISyntaxException;
}
