package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {
    private final String body;

    public HttpRequestBody(String body) {
        this.body = body;
    }

    public HttpRequestBody() {
        this.body = "";
    }

    public String getBody() {
        return body;
    }

    public int getLength() {
        return body.length();
    }

    public Map<String, String> getFormData() {
        HashMap<String, String> formData = new HashMap<>();

        String[] split = body.split("&");
        for (String data : split) {
            String[] keyValue = data.split("=");
            formData.put(keyValue[0], keyValue[1]);
        }
        return formData;
    }
}
