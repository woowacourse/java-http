package org.apache.catalina.handler;

import com.techcourse.model.UserService;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class ServletRequestHandler {

    private static final String SUCCESS_STATUS_CODE = "200 OK";
    private static final String STATIC_PATH_PREFIX = "static";
    private static final String TEXT_CONTENT_TYPE_PREFIX = "text/";
    private static final String HTML_TYPE = ".html";

    private final ViewResolver viewResolver;

    public ServletRequestHandler(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public Http11Response handle(Http11Request request) {
        final UserService userService = new UserService();
        final String requestURI = request.getRequestURI();
        final Http11Method httpMethod = request.getHttpMethod();

        if (Http11Method.GET.equals(httpMethod)) {
            Map<String, String> queryString = parseQueryString(requestURI);
            if (requestURI.contains("/login")) {
                userService.login(queryString.get("account"), queryString.get("password"));
                String type = parseTextContentType(HTML_TYPE);
                String path = parseStaticPath("/login.html");
                String body = viewResolver.resolve(path);
                return new Http11Response(SUCCESS_STATUS_CODE, type, body);
            }
            if (requestURI.equals("/")) {
                String type = parseTextContentType(HTML_TYPE);
                String body = "Hello world!";
                return new Http11Response(SUCCESS_STATUS_CODE, type, body);
            }
            if (requestURI.contains(".")) {
                String type = parseTextContentType(requestURI);
                String path = parseStaticPath(requestURI);
                String body = viewResolver.resolve(path);
                return new Http11Response(SUCCESS_STATUS_CODE, type, body);
            }
            String type = parseTextContentType(HTML_TYPE);
            return new Http11Response(SUCCESS_STATUS_CODE, type, requestURI);
        }
        if (Http11Method.POST.equals(httpMethod)) {
            String type = parseTextContentType(HTML_TYPE);
            return new Http11Response(SUCCESS_STATUS_CODE, type, requestURI);
        }
        throw new IllegalArgumentException("지원하지 않는 기능입니다.");
    }

    private Map<String, String> parseQueryString(String requestURI) {
        Map<String, String> queries = new HashMap<>();
        if (requestURI.contains("?")) {
            int index = requestURI.indexOf("?");
            String queryString = requestURI.substring(index + 1);
            for (String eachQueryString : queryString.split("&")) {
                String[] parsedEachQueryString = eachQueryString.split("=");
                queries.put(parsedEachQueryString[0], parsedEachQueryString[1]);
            }
        }
        return queries;
    }

    private String parseTextContentType(String filePath) {
        return TEXT_CONTENT_TYPE_PREFIX + filePath.split("\\.")[1];
    }

    private String parseStaticPath(String filePath) {
        return STATIC_PATH_PREFIX + filePath;
    }
}
