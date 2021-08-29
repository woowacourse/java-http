package nextstep.jwp.model.httpmessage.response;

import nextstep.jwp.model.httpmessage.common.CommonHttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseHeader extends CommonHttpHeader {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHeader.class);

    private final Map<ResponseHeaderType, String> headers = new LinkedHashMap<>();

    public void add(ResponseHeaderType type, String value) {
        headers.put(type, value);
        LOG.debug("Response header : {}: {}", type.value(), value);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        headers.forEach((key, value) -> stringJoiner.add(key.value() + ": " + value + " "));
        getCommonHeaders().forEach((key, value) -> stringJoiner.add(key.value() + ": " + value + " "));
        return stringJoiner.toString();
    }
}
