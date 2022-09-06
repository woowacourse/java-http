package org.apache.coyote.http11;

import static nextstep.jwp.http.StatusCode.matchStatusCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.QueryParams;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.utils.FileUtils;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String FILE_EXTENSION_SEPARATOR = ".";

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
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest httpRequest = extractRequest(bufferedReader);
            HttpResponse httpResponse = handle(httpRequest);

            outputStream.write(httpResponse.writeResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest extractRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        HttpHeaders httpHeaders = parseHttpHeaders(bufferedReader);
        String requestBody = parseRequestBody(bufferedReader, httpHeaders);

        return HttpRequest.from(requestLine, httpHeaders, requestBody);
    }

    private HttpHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            lines.add(line);
        }
        return HttpHeaders.parse(lines);
    }

    private String parseRequestBody(BufferedReader bufferedReader, HttpHeaders httpHeaders)
        throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.get("Content-Length").orElse("0"));

        char[] body = new char[contentLength];
        bufferedReader.read(body);
        return new String(body);
    }

    private HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        QueryParams queryParams = httpRequest.getQueryParams();
        if ("/".equals(path)) {
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_PLAIN, "Hello world!");
        }
        if ("/login".equals(path)) {
            return login(queryParams);
        }
        if (httpRequest.equals(ContentType.TEXT_PLAIN)) {
            String filePath =
                path + FILE_EXTENSION_SEPARATOR + ContentType.TEXT_HTML.getFileExtension();
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource(filePath)));
        }
        return readFile(httpRequest);
    }

    private HttpResponse login(QueryParams queryParams) {
        if (queryParams.count() == 0) {
            return HttpResponse.of(StatusCode.OK, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource("/login.html")));
        }
        if (LoginHandler.canLogin(queryParams)) {
            return HttpResponse.of(StatusCode.FOUND, ContentType.TEXT_HTML,
                FileUtils.readFile(getResource("/index.html")));
        }
        return HttpResponse.of(StatusCode.UNAUTHORIZED, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/401.html")));
    }

    private HttpResponse readFile(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return HttpResponse.of(matchStatusCode(path), httpRequest.getFileExtension(),
            FileUtils.readFile(getResource(path)));
    }

    private URL getResource(String uri) {
        URL resource = FileUtils.getResource(uri);
        if (resource == null) {
            log.error("올바르지 않은 경로: " + uri);
            return getResource("/404.html");
        }
        return resource;
    }
}
