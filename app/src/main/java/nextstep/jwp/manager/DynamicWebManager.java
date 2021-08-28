package nextstep.jwp.manager;

import nextstep.jwp.request.ClientRequest;

import java.io.OutputStream;

public class DynamicWebManager {
    public boolean canHandle(ClientRequest clientRequest) {
        return false;
    }

    public void handle(ClientRequest clientRequest, OutputStream outputStream) {

    }
}
