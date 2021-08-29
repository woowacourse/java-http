package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.model.httpmessage.common.CommonHttpHeader;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestHeader extends CommonHttpHeader {
    private final Map<RequestHeaderType, String> headers = new LinkedHashMap<>();

    public void add(String type, String value) {
        if (RequestHeaderType.contains(type)) {
            headers.put(RequestHeaderType.of(type), value);
        }

        if (commonHeaderContains(type)) {
            super.addHeader(type, value);
        }
    }

    @Override
    public String getHeader(String type) {
        if (RequestHeaderType.contains(type)) {
            return headers.get(RequestHeaderType.of(type));
        }

        if (commonHeaderContains(type)) {
            return super.getHeader(type);
        }

        throw new IllegalStateException("해당 요청 헤더 타입이 존재하지 않습니다. (입력 : " + type + ")");
    }
}
