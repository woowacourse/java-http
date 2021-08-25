package nextstep.jwp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.RequestHeaders;
import nextstep.jwp.http.ResponseEntity;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.http.ViewResolver;
import nextstep.jwp.service.HttpService;

public class HttpServlet {
    private final HttpService service;
    private final ViewResolver resolver;
    private final RequestHeaders headers;

    public HttpServlet(InputStream inputStream) throws IOException {
        this.service = new HttpService();
        this.resolver = new ViewResolver();
        this.headers = new RequestHeaders(inputStream);
    }

    public String getResponse() throws IOException {
        final String uri = headers.getUri();
        if ("/".equals(uri)) {
            return ResponseEntity
                    .responseBody("Hello world!")
                    .build();
        }

        if (headers.isQueryString()) {
            Map<String, String> params = headers.getParams();
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
