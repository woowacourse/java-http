package nextstep.jwp.web.http.request;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class HttpRequestHeaderValues {

    private static final String COMMA = ",";

    private final List<String> values;

    public HttpRequestHeaderValues(String... values) {
        this(Arrays.stream(values)
            .collect(toList()));
    }

    public HttpRequestHeaderValues(List<String> values) {
        validateBlankOrEmpty(values);
        List<String> trimValues = trimValues(values);
        validateContainsComma(trimValues);
        this.values = trimValues;
    }

    private void validateBlankOrEmpty(List<String> values) {
        if (values.stream().anyMatch(this::isNullOrEmpty)) {
            throw new RuntimeException("HttpRequestHeader의 value로 null 혹은 공백이 올 수 없습니다.");
        }
    }

    private void validateContainsComma(List<String> values) {
        if (values.stream().anyMatch(value -> value.contains(COMMA))) {
            throw new RuntimeException("HttpRequestHeader의 value에 \",\"가 포함될 수 없습니다.");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) || value.isBlank();
    }

    public HttpRequestHeaderValues add(String value) {
        return add(List.of(value));
    }

    public HttpRequestHeaderValues add(List<String> values) {
        return new HttpRequestHeaderValues(
            Stream.concat(this.values.stream(), trimValues(values).stream())
                .collect(toList())
        );
    }

    private List<String> trimValues(List<String> values) {

        return values.stream()
            .map(String::trim)
            .collect(toList());
    }

    public boolean isEmpty() {
        return list().isEmpty();
    }

    public List<String> list() {
        return values;
    }

    public String toValuesString() {
        return String.join(COMMA, values);
    }
}
