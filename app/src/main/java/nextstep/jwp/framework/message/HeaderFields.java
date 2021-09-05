package nextstep.jwp.framework.message;

import nextstep.jwp.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HeaderFields {

    private static final String BLANK = " ";
    private static final String HEADER_PIECE_SEPARATOR = "\r\n";
    private static final String HEADER_PARAM_SEPARATOR = ":";

    private final Map<String, String> params;

    private HeaderFields(Map<String, String> params) {
        this.params = params;
    }

    private HeaderFields() {
        this(new LinkedHashMap<>());
    }

    public static HeaderFields empty() {
        return new HeaderFields();
    }

    public static HeaderFields from(Map<String, String> fields) {
        return new HeaderFields(fields);
    }

    public static HeaderFields from(String headerString) {
        return from(
                StringUtils.extractMap(headerString, HEADER_PIECE_SEPARATOR, HEADER_PARAM_SEPARATOR)
        );
    }

    public String asString() {
        return params.entrySet().stream()
                .map(this::combineField)
                .collect(Collectors.joining());
    }

    private String combineField(Map.Entry<String, String> field) {
        return field.getKey() + HEADER_PARAM_SEPARATOR + BLANK + field.getValue() + HEADER_PIECE_SEPARATOR;
    }

    public void put(String key, String value) {
        this.params.put(key, value);
    }

    public Optional<String> take(String key) {
        return Optional.ofNullable(params.get(key));
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderFields that = (HeaderFields) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
