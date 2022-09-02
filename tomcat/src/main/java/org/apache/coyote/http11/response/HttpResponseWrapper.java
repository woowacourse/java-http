package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseWrapper {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String GET = "GET";

    private static final String ROOT = "static";

    private static final List<String> PAGES = List.of("/index.html", "/login.html", "register.html");
    private static final List<String> INIT_PAGES = List.of("/", "");

    private static final String OK = "200 OK";
    private static final String NOT_FOUND = "404 NOT FOUND";
    private static final String SEPARATOR = "";

    private final HttpRequestWrapper request;
    private HttpResponseHeader header;
    private HttpResponseBody body;


    public HttpResponseWrapper(HttpRequestWrapper request) {
        this.request = request;
        parseResponse();
    }

    private void parseResponse() {
        try {
            if (request.getMethod().equals(GET)) {
                String path = request.getPath();
                if (INIT_PAGES.contains(path)) {
                    header = new HttpResponseHeader(OK);
                    body = new HttpResponseBody("Hello world!");
                    return;
                }
                if (PAGES.contains(path)) {
                    header = new HttpResponseHeader(OK);
                    body = new HttpResponseBody(fileToResponseBody(ROOT + path));
                    return;
                }
                throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + path);
            }
            throw new NoSuchElementException("해당 메소드를 찾을 수 없습니다: " + request.getMethod());
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            header = new HttpResponseHeader(NOT_FOUND);
            body = new HttpResponseBody(fileToResponseBody(ROOT + "/404.html"));
        }
    }

    public String getHeader() {
        return String.join("\r\n",
                header.getHeaders(),
                header.getContentLengthHeader(body.getContentLength()));
    }

    public String getResponse() {
        return String.join("\r\n",
                header.getHeaders(),
                header.getContentLengthHeader(body.getContentLength()),
                SEPARATOR,
                body.getResponseBody());
    }

    private String fileToResponseBody(String fileName) {
        try {
            URL url = getClass().getClassLoader().getResource(fileName);
            Path path = new File(url.getFile()).toPath();
            return String.join("\r\n", new String(Files.readAllBytes(path)));
        } catch (NullPointerException | IOException e) {
            throw new NoSuchElementException("해당 이름의 파일을 찾을 수 없습니다: " + fileName);
        }
    }
}
