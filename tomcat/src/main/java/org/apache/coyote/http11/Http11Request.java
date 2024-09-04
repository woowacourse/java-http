package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Http11Request(String method, String path, Map<String, String> parameters, String protocolVersion,
                            String body) {

    public Http11Request(String method, String path, Map<String, String> parameters, String protocolVersion) {
        this(method, path, parameters, protocolVersion, null);
    }

    public static Http11Request parse(List<String> lines) {
        String[] startLineParts = lines.getFirst().split(" ");
        String method = startLineParts[0];
        String path = "";
        Map<String, String> parameters = Map.of();
        String protocolVersion = startLineParts[2];
        String body = null;

        Pattern pattern = Pattern.compile("([^?]+)(\\?(.*))?");
        Matcher matcher = pattern.matcher(startLineParts[1]);

        if (matcher.find()) {
            path = matcher.group(1);
            parameters = extractParameters(matcher.group(3));
        }

        if (lines.size() > 1 && lines.get(lines.size() - 2).isEmpty()) {
            body = lines.getLast();
        }

        return new Http11Request(method, path, parameters, protocolVersion, body);
    }

    public static Map<String, String> extractParameters(String query) {
        Map<String, String> parameters = new HashMap<>();

        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = URLDecoder.decode(keyValue[1], Charset.defaultCharset());
                    parameters.put(key, value);
                }
            }
        }

        return parameters;
    }
}
