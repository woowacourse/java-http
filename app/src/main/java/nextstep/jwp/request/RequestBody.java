package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {
    private final Map<String, String> body = new HashMap<>();

    public RequestBody(BufferedReader reader, int contentLength) throws IOException {
        parseBody(reader, contentLength);
    }

    private void parseBody(BufferedReader reader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String rawParams = new String(buffer);
        String[] params = rawParams.split("&");

        for (String param : params) {
            String[] keyAndValue = param.split("=");
            body.put(keyAndValue[0], keyAndValue[1]);
        }
    }

    public String getParam(String key) {
        if (body.containsKey(key)) {
            return body.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 인자입니다.");
    }
}
