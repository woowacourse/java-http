package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Request object. see <a href=https://datatracker.ietf.org/doc/html/rfc2616#section-5>RFC 2616, section 5</a>
 */
public class HttpRequest {

    private static final String SP = " ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ":";
    private static final int BUFFER_SIZE = 64;

    private final HttpMethod method;
    private final URI uri;
    private final String version;
    // Header field names are case-insensitive
    private final Map<String, String> header = new HashMap<>();
    private final String body;

    public HttpRequest(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            // Parse Request-Line
            String requestLine = bufferedReader.readLine();
            String[] tokens = requestLine.split(SP);
            method = HttpMethod.from(tokens[0]);
            uri = URI.create(tokens[1]);
            version = tokens[2];

            // Parse Header
            String headerLine;
            while (!(headerLine = bufferedReader.readLine()).isEmpty()) {
                String[] headerToken = headerLine.split(HEADER_DELIMITER, 2);
                this.header.put(headerToken[0].trim(), headerToken[1].trim());
            }

            // Parse Body, it should consider all character including escape characters
            long contentLength = Long.parseLong(header.getOrDefault(CONTENT_LENGTH, "0"));
            StringBuilder bodyBuilder = new StringBuilder();
            char[] buffer = new char[BUFFER_SIZE];
            int readBytes = 0;
            while (readBytes < contentLength) {
                readBytes += bufferedReader.read(buffer);
                bodyBuilder.append(buffer, 0, readBytes);
            }
            body = bodyBuilder.toString();

        } catch (IOException | IndexOutOfBoundsException e) {
            // IOException on reading lines using inputstream, parsing uri
            // IndexOutOfBoundsException on missing tokens in request-line and header
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

    public Map<String, String> getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String toString() {
        return "HttpRequest{" +
               "method=" + method +
               ", uri=" + uri +
               ", version='" + version + '\'' +
               ", header=" + header +
               ", body='" + body + '\'' +
               '}';
    }
}
