package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.HttpRequestBody;

public class Http11RequestBodyParser {

    public static HttpRequestBody parse(String encodedBody) {
        Map<String, String> parameters = new HashMap<>();
        for (String parameter : encodedBody.split("&")) {
            int index = parameter.indexOf("=");
            if (index == -1) {
                break;
            }
            String key = parameter.substring(0, index).trim();
            String value = parameter.substring(index + 1).trim();
            parameters.put(key, value);
        }
        return new HttpRequestBody(parameters);
    }
}
