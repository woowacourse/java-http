package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonProperties {

    private final Map<String, String> jsonProperties;

    public JsonProperties(Map<String, String> jsonProperties) {
        this.jsonProperties = jsonProperties;
    }

    private static JsonProperties fromUrlEncodedForm(String body) {
        return new JsonProperties(Arrays.stream(body.split("&"))
                                        .map(jsonProperty ->
                                                {
                                                    final var keyValue = jsonProperty.split("=");
                                                    return Map.entry(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8), URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                                                }
                                        )
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public static JsonProperties from(String body, HttpHeaders headers) {
        if (headers.isContentTypeUrlEncoded()) {
            return fromUrlEncodedForm(body);
        }
        return fromJsonForm(body);
    }

    private static JsonProperties fromJsonForm(String body) {
        return new JsonProperties(
                Arrays.stream(body.split(","))
                      .map(jsonProperty ->
                              {
                                  final var keyValue = jsonProperty.split(":");
                                  return Map.entry(keyValue[0].trim().replace("\"", ""), keyValue[1].trim().replace("\"", ""));
                              }
                      )
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public String getValue(String key) {
        return jsonProperties.get(key);
    }
}
