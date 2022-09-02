package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CSS;
import static org.apache.coyote.Constants.HTML;
import static org.apache.coyote.Constants.IMG;
import static org.apache.coyote.Constants.JS;
import static org.apache.coyote.Constants.ROOT;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request.HttpRequestWrapper;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseWrapper {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private HttpResponseHeader header;
    private HttpResponseBody body;


    public HttpResponseWrapper(HttpRequestWrapper request) {
        try {
            parseResponse(request);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            body = HttpResponseBody.of(ROOT + "/404.html");
            header = new HttpResponseHeader(HttpStatus.NOT_FOUND.getValue())
                    .addContentType("text/html")
                    .addContentLength(body.getContentLength());
        }
    }

    private void parseResponse(HttpRequestWrapper request) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String path = request.getPath();

        saveBodyIfMeet(method == GET && List.of("/", "").contains(path), "text/html", "/index.html");
        saveBodyIfMeet(method == GET && path.split("\\?")[0].equals("/login"), "text/html", "/login.html");

        saveBodyIfMeet(method == GET && HTML.contains(path), "text/html", path);
        saveBodyIfMeet(method == GET && JS.contains(path), "text/plain", path);
        saveBodyIfMeet(method == GET && CSS.contains(path), "text/css", path);
        saveBodyIfMeet(method == GET && IMG.contains(path), "image/svg+xml", path);

        if (header == null || body == null) {
            throw new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + path);
        }
    }

    private void saveBodyIfMeet(boolean condition, String contentType, String responseBodyPath) {
        if (condition) {
            body = HttpResponseBody.of(ROOT + responseBodyPath);
            header = new HttpResponseHeader(HttpStatus.OK.getValue())
                    .addContentType(contentType)
                    .addContentLength(body.getContentLength());
        }
    }

    public String getHeader() {
        return header.getHeaders();
    }

    public String getResponse() {
        return String.join("\r\n", getHeader(), "", body.getBodyContext());
    }
}
