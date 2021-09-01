package nextstep.jwp.http.common;

import nextstep.jwp.exception.NotFoundHeaderException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Headers {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, List<String>> values;

    public Headers() {
        values = new LinkedHashMap<>();
    }

    public Headers(Map<String, List<String>> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public static Headers of(BufferedReader reader) throws IOException {
        Headers headers = new Headers();
        String line = reader.readLine();
        while (!line.isBlank()) {
            String[] keyAndValue = line.split(HEADER_DELIMITER);
            headers.addHeader(keyAndValue[0].trim(), keyAndValue[1].trim());
            line = reader.readLine();
        }

        return headers;
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
}
