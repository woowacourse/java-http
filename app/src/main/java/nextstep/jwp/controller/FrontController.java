package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.http.RequestBody;
import nextstep.jwp.http.RequestHeader;
import nextstep.jwp.http.RequestLine;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.service.HttpService;

public class FrontController {
    private final RequestLine requestLine;
    private final HttpService service;
    private final RequestHeader headers;
    private final RequestBody body;

    public FrontController(RequestBody body, RequestHeader headers, RequestLine requestLine) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.service = new HttpService();
    }


    public String response() throws IOException {
        final String httpMethod = requestLine.getHttpMethod();
        final String uri = requestLine.getUri();
        if (HttpMethod.GET.equals(httpMethod)) {
            return getMapping(uri);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            return postMapping(uri);
        }

        throw new HttpException("설정되어 있지 않은 http 메소드입니다.");
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

    private String postMapping(String uri) throws IOException {
        if ("/register".equals(uri)) {
            Map<String, String> params = body.getParams();
            service.register(params);
            return ResponseEntity
                    .statusCode(StatusCode.FOUND)
                    .responseResource("/index.html")
                    .build();
        }

        if("/login".equals(uri)){
            Map<String, String> params = body.getParams();
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
        throw new HttpException("올바르지 않은 post 요청입니다.");
    }
}
