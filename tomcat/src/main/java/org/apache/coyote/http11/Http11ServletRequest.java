package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Http11ServletRequest(String method, String path, Map<String, String> parameters, String protocolVersion) {

    public static Http11ServletRequest parse(List<String> lines) {
        String[] startLineParts = lines.getFirst().split(" ");
        String method = startLineParts[0];
        String path = "";
        Map<String, String> parameters = new HashMap<>();
        String protocolVersion = startLineParts[2];

        Pattern pattern = Pattern.compile("([^?]+)(\\?(.*))?");
        Matcher matcher = pattern.matcher(startLineParts[1]);

        if (matcher.find()) {
            path = matcher.group(1);

            String queryString = matcher.group(3);
            if (queryString != null) {
                String[] pairs = queryString.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = URLDecoder.decode(keyValue[1], Charset.defaultCharset());
                        parameters.put(key, value);
                    }
                }
            }
        }

        return new Http11ServletRequest(method, path, parameters, protocolVersion);
    }
}
