package nextstep.jwp.infrastructure.http.objectmapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlEncodingMapper implements DataMapper {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_AND_VALUE_DELIMITER = "=";

    @Override
    public Map<String, String> parse(final String data) {
        final Map<String, String> result = new HashMap<>();
        final String[] parameters = data.split(PARAMETER_DELIMITER);

        for (final String parameter : parameters) {
            List<String> keyAndValue = Arrays.stream(parameter.split(KEY_AND_VALUE_DELIMITER))
                .collect(Collectors.toList());

            if (keyAndValue.size() != 2) {
                throw new IllegalArgumentException("Invalid format.");
            }

            result.put(keyAndValue.get(0), keyAndValue.get(1));
        }

        return result;
    }
}
