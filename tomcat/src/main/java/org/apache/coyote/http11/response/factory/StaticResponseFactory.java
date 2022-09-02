package org.apache.coyote.http11.response.factory;

import static org.apache.coyote.Constants.ROOT;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.coyote.Constants;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.apache.coyote.http11.response.element.HttpStatus;

public enum StaticResponseFactory {
    HTML(Constants.HTML, "text/html"),
    CSS(Constants.CSS, "text/css"),
    JS(Constants.JS, "text/plain"),
    IMG(Constants.IMG, "image/svg+xml");

    private final List<String> paths;
    private final String contentType;

    StaticResponseFactory(List<String> paths, String contentType) {
        this.paths = paths;
        this.contentType = contentType;
    }

    public static HttpResponse getResponse(HttpMethod method, String path) {
        if (method != HttpMethod.GET) {
            throw new NoSuchElementException();
        }
        HttpStatus status = HttpStatus.OK;
        StaticResponseFactory collector = Arrays.stream(values())
                .filter(name -> name.paths.stream().anyMatch(e -> e.equals(path)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 페이지를 찾을 수 없습니다: " + path));
        return to(status, path, collector.contentType);
    }

    public static HttpResponse to(HttpStatus status, String path, String contentType) {
        return HttpResponse.from(HttpResponseBody.of(ROOT + path), status, contentType);
    }

    public List<String> getPaths() {
        return paths;
    }
}
