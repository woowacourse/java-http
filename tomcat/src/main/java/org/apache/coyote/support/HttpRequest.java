package org.apache.coyote.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import support.IoUtils;
import support.StringUtils;

public class HttpRequest {

    private static final String HEADER_DELIMINATOR = ": ";
    private String httpMethod = "";
    private String uri = "";
    private Map<String, String> header = new HashMap<>();
    private String body = "";
    private Session session;


    public HttpRequest(final BufferedReader reader) {
        final String firstLineHeader = IoUtils.readLine(reader);
        final String[] split = firstLineHeader.split("\\s+");
        httpMethod = split[0];
        uri = split[1];
        header = createHeader(reader);
        body = readBody(reader, header.get("Content-Length"));
        session = getSessionOrNull();
    }

    private Session getSessionOrNull() {
        final String jSessionId = header.get(HttpCookie.HEADER_CONSTANT);
        if (jSessionId != null) {
            return new Session(jSessionId);
        }
        return null;
    }

    private String readBody(final BufferedReader reader, final String contentLength) {
        if (contentLength != null) {
            return IoUtils.readUrlEncoded(reader, Integer.parseInt(contentLength));
        }
        return "";
    }

    private Map<String, String> createHeader(final BufferedReader reader) {
        final Map<String, String> header = new HashMap<>();
        try {
            while (reader.ready()) {
                final String keyValueString = reader.readLine();
                if (keyValueString.contains(HEADER_DELIMINATOR)) {
                    final String[] keyValuePair = keyValueString.split(HEADER_DELIMINATOR);
                    final String key = keyValuePair[0];
                    final String value = keyValuePair[1];
                    header.put(key, value);
                } else if (StringUtils.isEmpty(keyValueString)) {
                    break;
                }
            }
            return header;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public Session getSession(final boolean create) {
        if (create) {
            return createOrGetSession();
        }
        return nullOrGetSession();
    }

    private Session createOrGetSession() {
        if (session != null) {
            return session;
        }
        return new Session(HttpCookie.ofRandomUuid().value());
    }

    private Session nullOrGetSession() {
        if (session != null) {
            return session;
        }
        return null;
    }

    public boolean isSame(String httpMethod, String uri) {
        return Objects.equals(this.httpMethod, httpMethod) && Objects.equals(this.uri, uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpMethod='" + httpMethod + '\'' +
                ", uri='" + uri + '\'' +
                ", header=" + header +
                ", body='" + body + '\'' +
                '}';
    }
}
