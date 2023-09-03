package org.apache.coyote.http11;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public enum RequestMappingHandler {

    STRING(RequestMappingHandler::isStringGetUrl, new HelloResponseMaker()),
    FILE(RequestMappingHandler::isFileGetUrl, new FileGetResponseMaker()),
    LOGIN_GET(RequestMappingHandler::isLoginGetUrl, new LoginGetResponseMaker()),
    LOGIN_POST(RequestMappingHandler::isLoginPostUrl, new LoginPostResponseMaker()),
    REGISTER_POST(RequestMappingHandler::isRegisterGetUrl, new RegisterGetResponseMaker());

    private static final Pattern FILE_REGEX = Pattern.compile(".+\\.(html|css|js|ico)");

    private final BiPredicate<String, String> condition;
    private final ResponseMaker responseMaker;

    RequestMappingHandler(final BiPredicate<String, String> condition, final ResponseMaker responseMaker) {
        this.condition = condition;
        this.responseMaker = responseMaker;
    }

    public static ResponseMaker findResponseMaker(final String request) throws URISyntaxException {
        String[] requestLines = request.split("\\s+");
        System.out.println("size: "+requestLines.length);

//        if (requestLines.length < 2) {
//            throw new UncheckedServletException(new Exception("예외"));
//        }

        URI uri = new URI(requestLines[1]);
        String resourcePath = uri.getPath();
        String requestMethod = requestLines[0];

        System.out.println("resourcePath: "+resourcePath);
        System.out.println("requestMethod: "+requestMethod);
        return Arrays.stream(values())
                .filter(value -> value.condition.test(resourcePath, requestMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 url 요청입니다."))
                .getResponseMaker();
    }

    public static boolean isFileGetUrl(final String resourcePath, final String requestMethod) {
        return FILE_REGEX.matcher(resourcePath).matches() && requestMethod.equals("GET");
    }

    public static boolean isStringGetUrl(final String resourcePath, final String requestMethod) {
        return resourcePath.equals("/") && requestMethod.equals("GET");
    }

    public static boolean isLoginGetUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.startsWith("/login") && requestMethod.equals("GET");
    }

    public static boolean isLoginPostUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.startsWith("/login") && requestMethod.equals("POST");
    }

    public static boolean isRegisterGetUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.startsWith("/register") && requestMethod.equals("GET");
    }

    public ResponseMaker getResponseMaker() {
        return responseMaker;
    }
}
