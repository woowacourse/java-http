package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.response.StaticResponse;
import nextstep.jwp.util.HeaderLine;

public class StaticController implements Controller {

    @Override
    public String process(HeaderLine headerLine) throws IOException {
        final String resourceType = headerLine.resourceType();
        final String path = headerLine.getRequestURLWithoutQuery();
        final StaticResponse staticResponse = new StaticResponse(resourceType, path);
        return staticResponse.response();
    }
}
