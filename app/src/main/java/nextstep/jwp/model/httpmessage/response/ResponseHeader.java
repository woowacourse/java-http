package nextstep.jwp.model.httpmessage.response;

import nextstep.jwp.model.httpmessage.common.CommonHttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader extends CommonHttpHeader {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHeader.class);

    private final Map<ResponseHeaderType, String> headers = new LinkedHashMap<>();

    public void add(ResponseHeaderType type, String value) {
        headers.put(type, value);
    }

    public Map<Object, String> getAllHeaders() {
        Map<Object, String> results = new LinkedHashMap<>();
        results.putAll(headers);
        results.putAll(getCommonHeaders());
        return results;
    }
}
