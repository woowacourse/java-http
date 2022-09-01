package org.apache.coyote.http11.utils;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.coyote.http11.FileResponseMaker;
import org.apache.coyote.http11.LoginResponseMaker;
import org.apache.coyote.http11.RegisterResponseMaker;
import org.apache.coyote.http11.ResponseMaker;
import org.apache.coyote.http11.StringResponseMaker;

public enum ResponseMakerFactory {

    STRING(ResponseMakerFactory::isStringUrl, new StringResponseMaker()),
    FILE(ResponseMakerFactory::isFileUrl, new FileResponseMaker()),
    LOGIN(ResponseMakerFactory::isLoginUrl, new LoginResponseMaker()),
    REGISTER(ResponseMakerFactory::isRegisterUrl, new RegisterResponseMaker());

    private static final Pattern FILE_REGEX = Pattern.compile(".+\\.(html|css|js|ico)");

    private final Predicate<String> condition;

    private final ResponseMaker responseMaker;

    ResponseMakerFactory(final Predicate<String> condition, final ResponseMaker responseMaker) {
        this.condition = condition;
        this.responseMaker = responseMaker;
    }

    public static ResponseMaker findResponseMaker(final String requestUrl) {
        return Arrays.stream(values())
                .filter(value -> value.condition.test(requestUrl))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 url 요청입니다."))
                .getResponseMaker();
    }

    public static boolean isFileUrl(final String requestUrl) {
        return FILE_REGEX.matcher(requestUrl).matches();
    }

    public static boolean isStringUrl(final String requestUrl) {
        return requestUrl.equals("/");
    }

    private static boolean isRegisterUrl(final String requestUrl) {
        return requestUrl.equals("/register");
    }

    public static boolean isLoginUrl(final String requestUrl) {
        return requestUrl.startsWith("/login");
    }

    public ResponseMaker getResponseMaker() {
        return responseMaker;
    }
}
