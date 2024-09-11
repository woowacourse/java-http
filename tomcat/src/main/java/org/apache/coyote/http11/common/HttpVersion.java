package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    HTTP_3_0("HTTP/3.0"),
    ;

    private final String expression;

    HttpVersion(String expression) {
        this.expression = expression;
    }

    public static HttpVersion from(String expression) {
        return Arrays.stream(values())
                .filter(version -> version.isExpressionMatch(expression))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 HTTP Version을 찾을 수 없습니다. 입력된 HTTP Version: " + expression));
    }

    private boolean isExpressionMatch(String expression) {
        return this.expression.equals(expression);
    }

    public String getExpression() {
        return expression;
    }
}
