package org.apache.coyote;

import java.io.InputStream;

public record HttpRequest(
        HttpMethod method,
        String uri
) {
    public static HttpRequest from(InputStream inputStream) {
        // TODO : 구현
        throw new UnsupportedOperationException();
    }

    /*
    private byte[] readHttpRequest(HttpRequest request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request));
        StringBuilder requestBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            requestBuilder.append(line).append("\r\n");
            if (line.isEmpty()) {
                break;
            }
        }

        return requestBuilder.toString().getBytes();
    }
     */

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        OPTIONS
    }
}
