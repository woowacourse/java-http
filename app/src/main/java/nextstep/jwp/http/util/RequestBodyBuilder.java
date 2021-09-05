package nextstep.jwp.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.request.RequestBody;

public class RequestBodyBuilder {

    private static final String EMPTY = "";
    private static final String EQUAL = "=";
    private static final String AND = "&";
    private static final String CONTENT_LENGTH = "Content-Length";

    private Map<String, String> requestBodies;
    private final BufferedReader bufferedReader;

    public RequestBodyBuilder(BufferedReader bufferedReader) {
        requestBodies = new LinkedHashMap<>();
        this.bufferedReader = bufferedReader;
    }

    public RequestBodyBuilder addBody(HttpHeaders httpHeaders) throws IOException {
        if (httpHeaders.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(httpHeaders.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            if (requestBody.isEmpty()) {
                return this;
            }
            String[] splitRequestBody = requestBody.split(AND);

            addBodies(splitRequestBody);
        }
        return this;
    }

    private void addBodies(String[] splitRequestBody) {
        for (String s : splitRequestBody) {
            String[] separateHeaderByColon = s.split(EQUAL);
            requestBodies.put(
                    separateHeaderByColon[0].trim(),
                    Objects.requireNonNullElse(separateHeaderByColon[1].trim(), EMPTY)
            );
        }
    }

    public RequestBody build() {
        return RequestBody.of(requestBodies);
    }

}
