package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.model.httpmessage.common.CommonHttpHeader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class RequestHeader extends CommonHttpHeader {
    private final Map<RequestHeaderType, String> headers = new LinkedHashMap<>();

    public void add(String type, String value) {
        if (RequestHeaderType.contains(type)) {
            headers.put(RequestHeaderType.of(type), value);
            return;
        }

        if (commonHeaderContains(type)) {
            super.addHeader(type, value);
            return;
        }

        throw new IllegalStateException("해당 요청 헤더 타입이 존재하지 않습니다. (입력 : " + type + ")");
    }

    public String getHeader(String type) {
        if (RequestHeaderType.contains(type)) {
            return headers.get(RequestHeaderType.of(type));
        }

        if (commonHeaderContains(type)) {
            return super.getHeader(type);
        }

        throw new IllegalStateException("해당 요청 헤더 타입이 존재하지 않습니다. (입력 : " + type + ")");
    }

    private void addAllHeaders(StringJoiner stringJoiner) {
        for (Map.Entry<RequestHeaderType, String> entry : headers.entrySet()) {
            String httpHeader = entry.getKey().value();
            String headerValue = entry.getValue();
            stringJoiner.add(httpHeader + ": " + headerValue + " ");
        }
    }

    public boolean containsKey(RequestHeaderType type) {
        return headers.containsKey(type);
    }
}
