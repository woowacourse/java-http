package nextstep.jwp.http.message;

import nextstep.jwp.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HeaderFields {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String HEADER_FIELD_SEPARATOR = ": ";

    private final Map<String, String> fields;

    public HeaderFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public static HeaderFields from(String headerFieldLines) {
        Map<String, String> headerFields = new LinkedHashMap<>();
        List<String> lines = StringUtils.splitWithSeparator(headerFieldLines, LINE_SEPARATOR);
        for (String line : lines) {
            List<String> pieces = StringUtils.splitTwoPiecesWithSeparator(line, HEADER_FIELD_SEPARATOR);
            String key = pieces.get(0).trim();
            String value = pieces.get(1).trim();
            headerFields.put(key, value);
        }
        return new HeaderFields(headerFields);
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
