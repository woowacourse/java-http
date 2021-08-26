package nextstep.jwp.http.request;

import nextstep.jwp.exception.IllegalOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestBody {

    private static final Logger LOG = LoggerFactory.getLogger(RequestBody.class);

    private final Map<String, String> queryParams;

    public RequestBody(String body) {
        validateNonNull(body);
        LOG.debug("requestBody : {}", body);
        queryParams = new ConcurrentHashMap<>();
        body = URLDecoder.decode(body, StandardCharsets.UTF_8);
        parseBody(body);
    }

    public static RequestBody empty() {
        return new RequestBody("");
    }

    private void validateNonNull(String body) {
        if (body == null) {
            throw new IllegalOperationException("body의 값이 null입니다.");
        }
    }

    private void parseBody(String body) {
        if (body.isBlank()) {
            return;
        }
        final String[] splitQueryParams = body.split("&");
        for (String singleQueryParam : splitQueryParams) {
            final String[] splitSingleQueryString = singleQueryParam.split("=");
            final String key = splitSingleQueryString[0];
            final String value = splitSingleQueryString[1];
            LOG.debug("Request body query params => key: {}, value: {}", key, value);
            queryParams.put(key, value);
        }
    }

    public String getParameter(String parameter) {
        return queryParams.get(parameter);
    }
}
