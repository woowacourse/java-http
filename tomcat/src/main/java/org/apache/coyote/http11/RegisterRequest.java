package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public record RegisterRequest(
    String account,
    String password,
    String email
) {

    public static RegisterRequest from(final String requestBody) {
        final Map<String, String> registerParams = new LinkedHashMap<>();
        Arrays.stream(requestBody.split("&"))
            .map(paramToken -> paramToken.split("="))
            .forEach(paramPair -> registerParams.put(
                URLDecoder.decode(paramPair[0], StandardCharsets.UTF_8),
                URLDecoder.decode(paramPair[1], StandardCharsets.UTF_8)));

        return new RegisterRequest(
            registerParams.get("account"),
            registerParams.get("password"),
            registerParams.get("email"));
    }

}
