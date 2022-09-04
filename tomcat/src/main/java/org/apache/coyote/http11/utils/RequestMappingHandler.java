package org.apache.coyote.http11.utils;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import org.apache.coyote.http11.FileGetResponseMaker;
import org.apache.coyote.http11.LoginGetResponseMaker;
import org.apache.coyote.http11.LoginPostResponseMaker;
import org.apache.coyote.http11.RegisterGetResponseMaker;
import org.apache.coyote.http11.RegisterPostResponseMaker;
import org.apache.coyote.http11.ResponseMaker;
import org.apache.coyote.http11.StringResponseMaker;

public enum RequestMappingHandler {

    STRING(RequestMappingHandler::isStringGetUrl, new StringResponseMaker()),
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

    public static ResponseMaker findResponseMaker(final String requestUrl, final String requestMethod) {
        return Arrays.stream(values())
                .filter(value -> value.condition.test(requestUrl, requestMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 url 요청입니다."))
                .getResponseMaker();
    }

    public static boolean isFileGetUrl(final String requestUrl, final String requestMethod) {
        return FILE_REGEX.matcher(requestUrl).matches() && requestMethod.equals("GET");
    }

    public static boolean isStringGetUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.equals("/") && requestMethod.equals("GET");
    }

    private static boolean isRegisterGetUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.equals("/register") && requestMethod.equals("GET");
    }

    private static boolean isRegisterPostUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.equals("/register") && requestMethod.equals("POST");
    }

    public static boolean isLoginGetUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.startsWith("/login") && requestMethod.equals("GET");
    }

    public static boolean isLoginPostUrl(final String requestUrl, final String requestMethod) {
        return requestUrl.startsWith("/login") && requestMethod.equals("POST");
    }

    public ResponseMaker getResponseMaker() {
        return responseMaker;
    }
}
