package nextstep.jwp.infrastructure.http.objectmapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlEncodingMapper implements DataMapper {

    @Override
    public Map<String, String> parse(final String data) {
        final Map<String, String> result = new HashMap<>();
        final String[] parameters = data.split("&");

        for (final String parameter : parameters) {
            List<String> keyAndValue = Arrays.stream(parameter.split("="))
                .collect(Collectors.toList());

            if (keyAndValue.size() != 2) {
                throw new IllegalArgumentException("Invalid format.");
            }

            result.put(keyAndValue.get(0), keyAndValue.get(1));
        }

        return result;
    }
}
