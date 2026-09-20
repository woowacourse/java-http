package org.apache.coyote.http11.request.body;

import org.apache.coyote.http11.request.vo.QueryParams;

public class RequestBody {

    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final String value;
    private final String contentType;

    public RequestBody(String value, String contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    public String getValue(String key) {
        return parse().getValue(key);
    }

    /**
     * NOTE
     * Tomcat 미션에서는 application/json으로 오는 json형식 RequestBody를 지원하지 않는다.
     * */
    private QueryParams parse() {
        if (contentType == null || contentType.equals(FORM_URLENCODED)) {
            return new QueryParams(value);
        }
        throw new IllegalArgumentException("지원하지 않는 Content-Type 입니다: " + contentType);
    }

}
