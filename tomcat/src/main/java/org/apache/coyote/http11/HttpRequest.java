package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP Request object. see <a href=https://datatracker.ietf.org/doc/html/rfc2616#section-5>RFC 2616, section 5</a>
 */
public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private static final String SP = " ";
    private static final String HEADER_DELIMITER = ":";
    private static final String QUESTION_MARK = "?";
    private static final int BUFFER_SIZE = 64;

    private final HttpMethod method;
    private final URI uri;
    private final QueryParameter queryParameter;
    private final String version;
    private final HttpHeaders headers = new HttpHeaders();
    private final String body;

    public HttpRequest(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            // Parse Request-Line
            String requestLine = bufferedReader.readLine();
            String[] tokens = requestLine.split(SP);
            method = HttpMethod.from(tokens[0]);

            int delimiterIndex = tokens[1].indexOf(QUESTION_MARK);
            if (delimiterIndex == -1) {
                uri = URI.create(tokens[1]);
                queryParameter = new QueryParameter(null);
            } else {
                uri = URI.create(tokens[1].substring(0, delimiterIndex));
                queryParameter = new QueryParameter(tokens[1].substring(delimiterIndex + 1));
            }
            version = tokens[2];

            // Parse Header
            String headerLine;
            while (!(headerLine = bufferedReader.readLine()).isEmpty()) {
                String[] headerToken = headerLine.split(HEADER_DELIMITER, 2);
                headers.put(headerToken[0].trim(), headerToken[1].trim());
            }

            // Parse Body, it should consider all character including escape characters
            StringBuilder bodyBuilder = new StringBuilder();
            char[] buffer = new char[BUFFER_SIZE];
            int readBytes = 0;
            while (readBytes < headers.getContentLength()) {
                readBytes += bufferedReader.read(buffer);
                bodyBuilder.append(buffer, 0, readBytes);
            }
            body = bodyBuilder.toString();

        } catch (IOException | IndexOutOfBoundsException e) {
            // IOException on reading lines using inputstream, parsing uri
            // IndexOutOfBoundsException on missing tokens in request-line and header
            log.error("Error parsing HTTP request", e);
            throw new IllegalArgumentException("Invalid HTTP request", e);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getParameter(String name) {
        return queryParameter.get(name);
    }

    public String getBody() {
        return body;
    }

    public String toString() {
        return "HttpRequest{" +
               "method=" + method +
               ", uri=" + uri +
               ", queryParameter=" + queryParameter +
               ", version='" + version + '\'' +
               ", header=" + headers +
               ", body='" + body + '\'' +
               '}';
    }
}
