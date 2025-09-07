package org.apache.coyote.http11.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.general.HttpBody;
import org.apache.coyote.http11.util.UriParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parseHttpRequest(BufferedReader bufferedReader) {
        try {
            HttpRequestHeaders headers = UriParser.parseHeaders(bufferedReader);
            HttpBody body = UriParser.parseBody(headers.getContentLength(), bufferedReader);
            return new HttpRequest(headers, body);
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            logger.error(exception.getMessage(), exception);
            return null;
        }
    }
}
