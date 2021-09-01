package nextstep.jwp.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.request.RequestBody;

public class RequestBodyBuilder {

    private Map<String, String> requestBodies;
    private final BufferedReader bufferedReader;

    public RequestBodyBuilder(BufferedReader bufferedReader) {
        requestBodies = new LinkedHashMap<>();
        this.bufferedReader = bufferedReader;
    }

    public RequestBodyBuilder addBody(HttpHeaders httpHeaders) throws IOException {
        if (httpHeaders.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            if (requestBody.isEmpty()) {
                return this;
            }
            String[] splitRequestBody = requestBody.split("&");

            addBodies(splitRequestBody);
        }
        return this;
    }

    private void addBodies(String[] splitRequestBody) {
        for (String s : splitRequestBody) {
            String[] separateHeaderByColon = s.split("=");
            requestBodies.put(separateHeaderByColon[0].trim(), separateHeaderByColon[1].trim());
        }
    }

    public RequestBody build() {
        return RequestBody.of(requestBodies);
    }

}
