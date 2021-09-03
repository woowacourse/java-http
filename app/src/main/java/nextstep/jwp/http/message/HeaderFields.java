package nextstep.jwp.http.message;

import nextstep.jwp.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HeaderFields {

    private static final String BLANK = " ";
    private static final String NEW_LINE = "\r\n";
    private static final String HEADER_FIELD_SEPARATOR = ":";

    private final Map<String, String> fields;

    public HeaderFields() {
        this.fields = new LinkedHashMap<>();
    }

    public HeaderFields(LinkedHashMap<String, String> fields) {
        this.fields = fields;
    }

    public static HeaderFields from(String headerFieldLines) {
        LinkedHashMap<String, String> headerFields = new LinkedHashMap<>();
        List<String> lines = StringUtils.splitWithSeparator(headerFieldLines, NEW_LINE);
        for (String line : lines) {
            List<String> pieces = StringUtils.splitTwoPiecesWithSeparator(line, HEADER_FIELD_SEPARATOR);
            String key = pieces.get(0).trim();
            String value = pieces.get(1).trim();
            headerFields.put(key, value);
        }
        return new HeaderFields(headerFields);
    }

    public String asString() {
        return fields.entrySet().stream()
                .map(this::combineField)
                .collect(Collectors.joining());
    }

    private String combineField(Map.Entry<String, String> field) {
        return field.getKey() + HEADER_FIELD_SEPARATOR + BLANK + field.getValue() + NEW_LINE;
    }

    public void put(String key, String value) {
        this.fields.put(key, value);
    }

    public Optional<String> take(String key) {
        return Optional.ofNullable(fields.get(key));
    }

    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HeaderFields that = (HeaderFields) o;
        return Objects.equals(getFields(), that.getFields());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFields());
    }
}
