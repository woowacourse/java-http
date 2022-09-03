package org.apache.coyote.http11.urihandler;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.UriResponse;

public interface UriHandler {

    boolean canHandle(String uri);

    UriResponse getResponse(String path, Map<String, Object> parameters) throws IOException;
}
