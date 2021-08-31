package nextstep.jwp.model.handler;

import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.http_response.JwpHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public class ResourceHandler extends DefaultHttpHandler {

    private static final String RESOURCE_PREFIX = "static";

    @Override
    public String handle(JwpHttpRequest jwpHttpRequest) throws IOException, URISyntaxException {
        String uri = jwpHttpRequest.getUri();
        String resourceFile = findResourceFile(RESOURCE_PREFIX + uri);
        return JwpHttpResponse.ok(uri, resourceFile);
    }
}
