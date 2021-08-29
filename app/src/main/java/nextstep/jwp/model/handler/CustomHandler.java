package nextstep.jwp.model.handler;

import nextstep.jwp.model.JwpHttpRequest;

import java.io.OutputStream;

public interface CustomHandler {

    void handle(JwpHttpRequest uri, OutputStream outputStream);
}
