package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import nextstep.jwp.http.RequestHeader;
import nextstep.jwp.http.RequestLine;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.service.HttpService;

public class HttpServer {
    private final HttpService service;
    private final ViewResolver resolver;
    private final RequestLine requestLine;
    private final RequestHeader headers;

    public HttpServer(InputStream inputStream) throws IOException {
        this.service = new HttpService();
        this.resolver = new ViewResolver();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = new RequestLine(extractRequestLine(bufferedReader));
        this.headers = new RequestHeader(extractHeaders(bufferedReader));
    }

    private String extractHeaders(BufferedReader bufferedReader) throws IOException {
        final StringBuilder request = new StringBuilder();
        String line = null;
        while (!"".equals(line)) {
            line = bufferedReader.readLine();

            if (line == null) {
                break;
            }

            request.append(line)
                    .append("\r\n");

        }
        return request.toString();
    }

    public String extractRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    public String getResponse() throws IOException {
        final String httpMethod = requestLine.getHttpMethod();
        final String uri = requestLine.getUri();
        if ("".equals(httpMethod)) {
            throw new IllegalArgumentException("null null~");
        }
        if ("GET".equals(httpMethod)) {
            return getMapping(uri);
        }
        throw new IllegalArgumentException("wlekrj");
    }

    private String getMapping(String uri) throws IOException {
        if ("/".equals(uri)) {
            return ResponseEntity
                    .responseBody("Hello world!")
                    .build();
        }

        if (requestLine.isQueryString()) {
            Map<String, String> params = requestLine.getParams();
            if (service.isAuthorized(params)) {
                return ResponseEntity
                        .statusCode(StatusCode.FOUND)
                        .responseBody(resolver.findResource("/index.html"))
                        .build();
            }
            return ResponseEntity
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseBody(resolver.findResource("/401.html"))
                    .build();
        }

        return ResponseEntity
                .responseBody(resolver.findResource(uri))
                .build();
    }

}
