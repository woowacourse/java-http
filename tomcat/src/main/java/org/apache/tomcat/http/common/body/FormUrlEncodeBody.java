package org.apache.tomcat.http.common.body;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FormUrlEncodeBody implements Body {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> content;

    public FormUrlEncodeBody(final String plaintext) {
        this.content = Stream.of(plaintext.split(PARAMETER_DELIMITER))
                .map(param -> List.of(param.split(KEY_VALUE_DELIMITER)))
                .filter(param -> param.size() == 2)
                .collect(Collectors.toMap(List::getFirst, param -> param.get(1)));
    }

    @Override
    public String getContent(final String key) {
        return content.getOrDefault(key, "");
    }

    @Override
    public String deserialize() {
        return content.keySet()
                .stream()
                .map(key -> String.join(KEY_VALUE_DELIMITER, key, content.get(key)))
                .collect(Collectors.joining(PARAMETER_DELIMITER));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FormUrlEncodeBody that = (FormUrlEncodeBody) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
