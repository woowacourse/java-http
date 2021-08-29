package nextstep.jwp.web.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    public static final String HEADER_DELIMITER = "\r\n";
    private final Map<String, List<String>> headerValues = new LinkedHashMap<>();

    public Headers() {
    }

    public Headers(List<String> rawHeaders) {
        for (String header : rawHeaders) {
            String[] keyAndValue = header.split(": ");
            String[] multiValues = keyAndValue[1].split(",");
            Arrays.stream(multiValues)
                .forEach(value -> add(keyAndValue[0], value.trim()));
        }
    }

    public void add(String header, String value) {
        List<String> values = this.headerValues.getOrDefault(header, new ArrayList<>());
        values.add(value.trim());

        this.headerValues.put(header, values);
    }

    public List<String> getValue(String key) {
        return headerValues.get(key);
    }

    public int getContentLength() {
        List<String> values = this.headerValues.getOrDefault("Content-Length", List.of("0"));
        return Integer.parseInt(values.get(0));
    }

    public String asString() {
        StringBuilder stringBuilder = new StringBuilder();

        headerValues.keySet()
            .forEach(key -> stringBuilder.append(key)
                .append(": ")
                .append(String.join(",", headerValues.get(key)))
                .append(HEADER_DELIMITER));

        return stringBuilder.toString();
    }
}
