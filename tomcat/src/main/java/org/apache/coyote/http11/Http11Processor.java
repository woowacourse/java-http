package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.ContentType;
import org.apache.coyote.Processor;
import org.apache.coyote.QueryParams;
import org.apache.coyote.support.ResourcesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final int URL_INDEX = 1;
    public static final String LANDING_PAGE_URL = "/";
    public static final String STATIC_PATH = "static";
    public static final String DEFAULT_EXTENSION = ".html";
    public static final String LOGIN_URL = "/login";
    public static final String QUERY_STRING_DELIMITER = "?";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String uri = parseUri(bufferedReader);
            String responseBody = accessUri(uri);
            ContentType contentType = parseContentType(uri);

            String response = toResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().split(" ", -1)[URL_INDEX];
    }

    private String accessUri(final String uri) throws IOException {
        if (uri.equals(LANDING_PAGE_URL)) {
            return "Hello world!";
        }
        String url = parseUrl(uri);
        String queryString = parseQueryString(uri);
        return accessUrl(url, QueryParams.parseQueryParams(queryString));
    }

    private String parseUrl(final String uri) {
        if (uri.equals(LANDING_PAGE_URL)) {
            return uri;
        }
        String url = cutQueryString(uri);
        String extension = ResourcesUtil.parseExtension(url);
        if (extension.isBlank()) {
            return url + DEFAULT_EXTENSION;
        }
        return url;
    }

    private String cutQueryString(final String uri) {
        int lastIndexOfQueryStringDelimiter = uri.lastIndexOf(QUERY_STRING_DELIMITER);
        if (lastIndexOfQueryStringDelimiter == -1) {
            return uri;
        }
        return uri.substring(0, lastIndexOfQueryStringDelimiter);
    }

    private String parseQueryString(final String uri) {
        if (uri.lastIndexOf(QUERY_STRING_DELIMITER) == -1) {
            return "";
        }
        return uri.substring(uri.lastIndexOf(QUERY_STRING_DELIMITER));
    }

    private String accessUrl(final String url, final QueryParams queryParams) throws IOException {
        if (url.equals(LANDING_PAGE_URL)) {
            return "Hello world!";
        }
        if (url.equals(LOGIN_URL)) {
            return "";
        }
        return ResourcesUtil.readResource(STATIC_PATH + url);
    }

    private ContentType parseContentType(final String uri) {
        String extension = ResourcesUtil.parseExtension(uri);
        return ContentType.fromExtension(extension);
    }

    private String toResponse(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
