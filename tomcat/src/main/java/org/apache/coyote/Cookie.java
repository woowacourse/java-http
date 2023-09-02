package org.apache.coyote;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Cookie {

    public Map<String, String> getCookies(String request) {
        String[] splitRequest = request.split("\r\n");
        return Arrays.stream(splitRequest)
                .filter(line -> line.startsWith("Cookie:"))
                .map(line -> line.replace("Cookie: ", ""))
                .map(line -> line.split(";"))
                .flatMap(Arrays::stream)
                .map(line -> line.split("="))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));
    }

    public String createCookie() {
        return "Set-Cookie: JSESSIONID=" + UUID.randomUUID().toString();
    }

}
