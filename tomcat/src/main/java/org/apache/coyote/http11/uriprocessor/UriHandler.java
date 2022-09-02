package org.apache.coyote.http11.uriprocessor;

import java.io.IOException;
import org.apache.coyote.http11.UriResponse;

public interface UriHandler {

    UriResponse getResponse(String uri) throws IOException;
}
