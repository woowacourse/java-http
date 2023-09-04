package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.*;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public enum RequestMappingHandler {

    STRING(RequestMappingHandler::isStringGetUrl, new HelloResponseMaker()),
    FILE(RequestMappingHandler::isFileGetUrl, new FileGetResponseMaker()),
    LOGIN_GET(RequestMappingHandler::isLoginGetUrl, new LoginGetResponseMaker()),
    LOGIN_POST(RequestMappingHandler::isLoginPostUrl, new LoginPostResponseMaker()),
    REGISTER_GET(RequestMappingHandler::isRegisterGetUrl, new RegisterGetResponseMaker()),
    REGISTER_POST(RequestMappingHandler::isRegisterPostUrl, new RegisterPostResponseMaker());

    private static final Pattern FILE_REGEX = Pattern.compile(".+\\.(html|css|js|ico)");

    private final BiPredicate<String, String> condition;
    private final ResponseMaker responseMaker;

    RequestMappingHandler(final BiPredicate<String, String> condition, final ResponseMaker responseMaker) {
        this.condition = condition;
        this.responseMaker = responseMaker;
    }

    public static ResponseMaker findResponseMaker(final HttpRequest request) {
        String resourcePath = request.getRequestLine().getRequestUrl();
        String requestMethod = request.getRequestLine().getHttpMethod();

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

    public static boolean isRegisterPostUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.startsWith("/register") && requestMethod.equals("POST");
    }

    public ResponseMaker getResponseMaker() {
        return responseMaker;
    }

}
