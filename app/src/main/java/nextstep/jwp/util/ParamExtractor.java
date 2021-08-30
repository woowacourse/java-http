package nextstep.jwp.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ParamExtractor {

    private ParamExtractor() {
    }

    public static Map<String, String> extractParams(String query) {
        String[] splitParams = query.split("&");

        return Arrays.stream(splitParams)
            .map(param -> param.split("="))
            .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}
