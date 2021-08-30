package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.model.httpmessage.common.CommonHttpHeader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RequestHeader extends CommonHttpHeader {
    private final Map<RequestHeaderType, String> headers = new LinkedHashMap<>();

    public void add(String type, String value) {
        Optional<RequestHeaderType> requestHeaderType = RequestHeaderType.of(type);
        if (requestHeaderType.isPresent()) {
            headers.put(requestHeaderType.get(), value);
            return;
        }

        if (commonHeaderContains(type)) {
            super.addHeader(type, value);
        }
    }

    @Override
    public String getHeader(String type) {
        Optional<RequestHeaderType> requestHeaderType = RequestHeaderType.of(type);
        if (requestHeaderType.isPresent()) {
            return headers.get(requestHeaderType.get());
        }

        if (commonHeaderContains(type)) {
            return super.getHeader(type);
        }

        throw new IllegalStateException("해당 요청 헤더 타입이 존재하지 않습니다. (입력 : " + type + ")");
    }
}
