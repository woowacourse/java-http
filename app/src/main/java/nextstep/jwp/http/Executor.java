package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.http.response.HttpResponse;

public class Executor {

    public Executor() {
    }

    public HttpResponse service(HttpRequest httpRequest) {
        return service(httpRequest.getRequestLine());
    }

    public HttpResponse service(RequestLine requestLine) {
        try {
            String method = requestLine.getMethod();
            RequestUriPath uriPath = requestLine.getUriPath();

            if (method.equalsIgnoreCase("GET")) {
                final URL resourceUrl = getClass().getResource("/static" + uriPath.getPath());
                final Path filePath = new File(resourceUrl.getFile()).toPath();
                final String responseBody = String.join("\n", Files.readAllLines(filePath)) + "\n";
                return HttpResponse.ok(responseBody);
            }
        } catch (IOException e) {
            return HttpResponse.ok("Hello world!");
        } catch (Exception e) {
            throw new IllegalArgumentException("적절한 HTTP 헤더 포맷이 아닙니다.");
        }

        // TODO :: ERROR TYPE, HTTP STATUS 고민
        return HttpResponse.error("");
    }
}
