package nextstep.jwp.http.common;

import com.google.common.net.HttpHeaders;
import nextstep.jwp.exception.NotFoundHeaderException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<String, List<String>> values;

    public Headers() {
        values = new LinkedHashMap<>();
    }

    public Headers(Map<String, List<String>> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public void addHeader(String key, String value) {
        if (this.values.containsKey(key)) {
            this.values.get(key).add(value);
            return;
        }

        List<String> headerValues = new ArrayList<>();
        headerValues.add(value);
        this.values.put(key, headerValues);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        values.entrySet()
                .forEach(entrySet -> sb.append(parsingHeaderLine(entrySet)).append(" \r\n"));

        return sb.toString();
    }

    private String parsingHeaderLine(Map.Entry<String, List<String>> entrySet) {
        return entrySet.getKey() + ": " + String.join(",", entrySet.getValue());
    }

    public String getHeaderValue(String header) {
        if (!values.containsKey(header)) {
            throw new NotFoundHeaderException(header);
        }

        List<String> headerValues = values.get(header);
        if (headerValues.size() > 1) {
            return String.join(",", headerValues);
        }

        return headerValues.get(0);
    }

    public boolean hasNoContent() {
        return !values.containsKey(HttpHeaders.CONTENT_LENGTH)
                || values.get(HttpHeaders.CONTENT_LENGTH).get(0).equals("0");
    }
}
