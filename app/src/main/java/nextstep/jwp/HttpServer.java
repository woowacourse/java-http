package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import nextstep.jwp.constants.Headers;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.RequestHeader;
import nextstep.jwp.http.RequestLine;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.service.HttpService;

public class HttpServer {
    private final HttpService service;
    private final RequestLine requestLine;
    private final RequestHeader headers;
    private final RequestBody body;

    public HttpServer(InputStream inputStream) throws IOException {
        this.service = new HttpService();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = new RequestLine(extractRequestLine(bufferedReader));
        this.headers = new RequestHeader(extractHeaders(bufferedReader));
        this.body = new RequestBody(extractRequestBody(bufferedReader));
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

    public String extractRequestBody(BufferedReader reader) throws IOException {
        if (headers.get(Headers.CONTENT_LENGTH).isPresent()) {
            int contentLength = Integer.parseInt(headers.get(Headers.CONTENT_LENGTH).get());
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return null;
    }

    public String getResponse() throws IOException {
        final String httpMethod = requestLine.getHttpMethod();
        final String uri = requestLine.getUri();
        if ("".equals(httpMethod)) {
            throw new HttpException("http 메소드가 없어요");
        }
        if ("GET".equals(httpMethod)) {
            return getMapping(uri);
        }
        if ("POST".equals(httpMethod)) {
            return postMapping(uri);
        }

        throw new HttpException("설정되어 있지 않은 http 메소드입니다.");
    }

    private String postMapping(String uri) throws IOException {
        if ("/register".equals(uri)) {
            Map<String, String> params = body.getParams();
            service.register(params);
            return ResponseEntity
                    .statusCode(StatusCode.FOUND)
                    .responseResource("/index.html")
                    .build();
        }
        throw new HttpException("올바르지 않은 post 요청입니다.");
    }

    private String getMapping(String uri) throws IOException {
        if ("/".equals(uri)) {
            return  ResponseEntity
                    .responseBody("Hello world!")
                    .build();
        }

        if (requestLine.isQueryString()) {
            Map<String, String> params = requestLine.getParams();
            if (service.isAuthorized(params)) {
                return ResponseEntity
                        .statusCode(StatusCode.FOUND)
                        .responseResource("/index.html")
                        .build();
            }
            return ResponseEntity
                    .statusCode(StatusCode.UNAUTHORIZED)
                    .responseResource("/401.html")
                    .build();
        }

        return ResponseEntity
                .responseResource(uri)
                .build();
    }

}
