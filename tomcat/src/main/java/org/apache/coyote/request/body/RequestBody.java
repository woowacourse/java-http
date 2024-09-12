package org.apache.coyote.request.body;

import org.apache.coyote.request.parser.QueryParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestBody {

    public static final String PARAM_SEPARATOR = "?";
    public static final int NO_PARAM_NUMBER = -1;
    public static final int PARAM_START_PLUS_INDEX = 1;

    private final Map<String, String> body;

    public RequestBody(String body) {
        this.body = parseBody(body);
    }

    private Map<String, String> parseBody(String body) {
        if (body == null) {
            return new HashMap<>();
        }
        return QueryParser.parse(getQueryString(body));
    }

    private String getQueryString(String body) {
        int index = body.indexOf(PARAM_SEPARATOR);

        if (index == NO_PARAM_NUMBER) {
            return body;
        }
        return body.substring(getParamStartIndex(index));
    }

    public String getValue(String key) {
        return body.get(key);
    }

    private int getParamStartIndex(int index) {
        return index + PARAM_START_PLUS_INDEX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestBody that = (RequestBody) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }
}
