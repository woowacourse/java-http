package org.apache.coyote.domain.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private final Map<String, String> requestBody;

    public RequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    public static RequestBody of(BufferedReader inputReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        inputReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new RequestBody(generateBody(requestBody));
    }

    private static Map<String, String> generateBody(String body) {
        Map<String, String> requestBody = new HashMap<>();
        if(body.length()==0){
            return requestBody;
        }
        String[] bodies = body.split("&");
        for (String data : bodies) {
            String[] keyAndValue = data.split("=");
            requestBody.put(keyAndValue[0], keyAndValue[1]);
        }
        return requestBody;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }
}
