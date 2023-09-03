package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;
import nextstep.jwp.exception.UncheckedServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private String method;

    private String uri;

    public static HttpRequest from(Socket connection) {
        try {
            final var inputStream = connection.getInputStream();
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
            final String httpRequestMethod = stringTokenizer.nextToken();
            final String httpRequestUri = stringTokenizer.nextToken();
            return new HttpRequest(httpRequestMethod, httpRequestUri);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public HttpRequest(final String method, final String uri) {
        this.method = method;
        this.uri = uri;
    }

    public boolean isMethodEqualTo(final String method) {
        return Objects.equals(this.method, method);
    }

    public boolean isUriEqualTo(final String uri) {
        return Objects.equals(this.uri, uri);
    }

    public boolean isJavascriptRequest() {
        return isMethodEqualTo("GET") && this.uri.startsWith("/js/");
    }

    public boolean isAssetRequest() {
        return isMethodEqualTo("GET") && this.uri.startsWith("/assets/");
    }

    public String getEndPoint() {
        return this.uri.substring(this.uri.lastIndexOf("/") + 1, this.uri.length());
    }
}
