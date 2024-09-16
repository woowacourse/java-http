package org.apache.coyote.http11.message.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.coyote.http11.message.common.HttpCookie;

public class HttpCookieParser {

    private static final String LINE_DELIMITER = ";";
    private static final String VALUE_DELIMITER = "=";
    private static final int PAIR = 2;

    private HttpCookieParser() {
    }

    public static HttpCookie parse(String cookies) {
        StringTokenizer tokenizer = new StringTokenizer(cookies, LINE_DELIMITER);
        Map<String, String> result = new HashMap<>();

        while (tokenizer.hasMoreTokens()) {
            StringTokenizer valueTokenizer = new StringTokenizer(tokenizer.nextToken(), VALUE_DELIMITER);
            if (valueTokenizer.countTokens() != PAIR) {
                throw new IllegalArgumentException("올바르지 않은 Cookie 형식입니다.");
            }
            result.put(valueTokenizer.nextToken().strip(), valueTokenizer.nextToken().strip());
        }
        return new HttpCookie(result);
    }
}
