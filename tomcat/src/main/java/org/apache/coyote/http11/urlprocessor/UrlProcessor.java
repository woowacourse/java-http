package org.apache.coyote.http11.urlprocessor;

import java.io.IOException;
import org.apache.coyote.http11.UrlResponse;

public interface UrlProcessor {

    UrlResponse getResponse(String url) throws IOException;
}
