package nextstep.jwp.http.reqeust;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private static final String BODY_CONNECTOR = "&";
    private static final String BODY_SEPARATOR = "=";
    private static final String EMPTY_REQUEST_BODY = " ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> values;

    public HttpRequestBody(final BufferedReader bufferReader, final String contentLength) throws IOException {
        this.values = new HashMap<>();

        String requestBody = requestBody(bufferReader, contentLength);

        if (!requestBody.isBlank()) {
            addValueFrom(requestBody);
        }
    }

    private String requestBody(final BufferedReader bufferReader, final String contentLength) throws IOException {
        if (contentLength == null) {
            return EMPTY_REQUEST_BODY;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferReader.read(buffer, 0, length);
        return new String(buffer);
    }

    private void addValueFrom(final String requestBody) {
        String[] bodies = requestBody.split(BODY_CONNECTOR);

        for (String body : bodies) {
            String[] value = body.split(BODY_SEPARATOR);
            this.values.put(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }

    public Map<String, String> getValues() {
        return values;
    }
}
