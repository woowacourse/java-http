package nextstep.jwp.model.httpMessage.response;

import nextstep.jwp.model.httpMessage.AbstractHttpHeader;
import nextstep.jwp.model.httpMessage.HttpHeaderType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseHeader extends AbstractHttpHeader {

    private Map<String, String> headers = new LinkedHashMap<>();

    public void add(HttpHeaderType type, String value) {
        headers.put(type.value(), value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        headers.forEach((key, value) -> stringJoiner.add(key + ": " + value + " "));
        return stringJoiner.toString();
    }
}
