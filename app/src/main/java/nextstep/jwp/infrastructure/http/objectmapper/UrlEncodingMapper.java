package nextstep.jwp.infrastructure.http.objectmapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlEncodingMapper implements DataMapper {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    @Override
    public Map<String, String> parse(final String data) {
        final Map<String, String> result = new HashMap<>();
        final String[] parameters = data.split(PARAMETER_DELIMITER);

        for (final String parameter : parameters) {
            List<String> keyAndValue = Arrays.stream(parameter.split(KEY_AND_VALUE_DELIMITER, 2))
                .collect(Collectors.toList());

            validateKeyAndValue(keyAndValue);

            result.put(keyAndValue.get(KEY_INDEX), keyAndValue.get(VALUE_INDEX));
        }

        return result;
    }

    private void validateKeyAndValue(final List<String> keyAndValue) {
        if (keyAndValue.size() != 2 || "".equals(keyAndValue.get(KEY_INDEX))) {
            throw new IllegalArgumentException("Invalid format.");
        }
    }
}
