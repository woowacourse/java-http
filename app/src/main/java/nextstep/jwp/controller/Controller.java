package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.service.Service;
import nextstep.jwp.util.HeaderLine;
import nextstep.jwp.util.URIs;

public class Controller {

    public static String process(HeaderLine headerLine) throws IOException {
        Service service = matchService(headerLine);
        return service.process(headerLine);
    }

    private static Service matchService(HeaderLine headerLine) {
        String uri = headerLine.getRequestURLWithoutQuery();
        return URIs.matchService(uri);
    }
}
