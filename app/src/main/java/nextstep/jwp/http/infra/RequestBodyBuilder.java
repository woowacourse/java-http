package nextstep.jwp.http.infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.request.RequestBody;

public class RequestBodyBuilder {

    private final HashMap<String, String> requestBodies;
    private final BufferedReader bufferedReader;

    public RequestBodyBuilder(BufferedReader bufferedReader) {
        requestBodies = new HashMap<>();
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
            for (int i = 0; i < splitRequestBody.length; i++) {
                String[] separateHeaderByColon = splitRequestBody[i].split("=");
                this.requestBodies.put(separateHeaderByColon[0], separateHeaderByColon[1].trim());
            }
        }
        return this;
    }

    public RequestBody build() {
        return RequestBody.of(requestBodies);
    }

}
