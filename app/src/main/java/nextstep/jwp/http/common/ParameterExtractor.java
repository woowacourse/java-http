package nextstep.jwp.http.common;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ParameterExtractor {

    private ParameterExtractor() {
    }

    public static Map<String, String> extract(String rawParams) {
        if(rawParams.contains("?")) {
            rawParams = rawParams.substring(rawParams.indexOf("?") + 1);
        }
        String[] splitParams = rawParams.split("&");

        return Arrays.stream(splitParams)
            .filter(param -> param.contains("="))
            .map(param -> param.split("="))
            .collect(toMap(param -> param[0], param -> param[1]));
    }
}
