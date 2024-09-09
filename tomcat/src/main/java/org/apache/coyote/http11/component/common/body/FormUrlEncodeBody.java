package org.apache.coyote.http11.component.common.body;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FormUrlEncodeBody implements Body<Map<String, String>> {

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
    public Map<String, String> serialize() {
        return content;
    }

    @Override
    public String deserialize() {
        return content.keySet()
                .stream()
                .map(key -> String.join(KEY_VALUE_DELIMITER, key, content.get(key)))
                .collect(Collectors.joining(PARAMETER_DELIMITER));
    }
}
