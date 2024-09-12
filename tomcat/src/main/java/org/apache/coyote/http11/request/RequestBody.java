package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

    private String body;

    public RequestBody() {
        this.body = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getUserInformation() {
        return Arrays.stream(body.split("&"))
                .map(line -> line.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .filter(keyValue -> !keyValue[0].isBlank() && !keyValue[1].isBlank())
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }
}
