package org.apache.coyote.request;

import org.apache.coyote.common.MessageBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestBody {

    private static final String REQUEST_DELIMITER = "&";
    private static final String NAME_VALUE_DELIMITER = "=";

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> body;

    private RequestBody(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBody from(final MessageBody messageBody) {
        final String namesAndValues = messageBody.body();
        if (Objects.isNull(namesAndValues) || namesAndValues.isBlank()) {
            return new RequestBody(Collections.emptyMap());
        }

        return new RequestBody(collectBodyMapping(namesAndValues));
    }

    private static Map<String, String> collectBodyMapping(final String namesAndValues) {
        return Arrays.asList(namesAndValues.split(REQUEST_DELIMITER))
                .stream()
                .map(bodyEntry -> bodyEntry.split(NAME_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        entry -> entry[NAME_INDEX],
                        entry -> entry[VALUE_INDEX]
                ));
    }

    public String getBodyValue(final String name) {
        return body.getOrDefault(name, null);
    }

    public List<String> bodyNames() {
        return new ArrayList<>(body.keySet());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestBody that = (RequestBody) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "RequestBody{" +
               "body=" + body +
               '}';
    }
}
