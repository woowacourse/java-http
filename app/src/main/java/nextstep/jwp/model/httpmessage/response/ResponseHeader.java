package nextstep.jwp.model.httpmessage.response;

import nextstep.jwp.model.httpmessage.common.CommonHttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader extends CommonHttpHeader {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHeader.class);

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void add(String type, String value) {
        headers.put(type, value);
    }

    public Map<String, String> getAllHeaders() {
        Map<String, String> results = new LinkedHashMap<>();
        results.putAll(headers);
        results.putAll(getCommonHeaders());
        return results;
    }
}
