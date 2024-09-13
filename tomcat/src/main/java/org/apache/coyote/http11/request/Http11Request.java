package org.apache.coyote.http11.request;

import java.util.List;

public final class Http11Request extends HttpRequest {

    private static final String VERSION = "http/1.1";

    public Http11Request(List<String> clientData) {
        super(clientData, VERSION);
    }

}
