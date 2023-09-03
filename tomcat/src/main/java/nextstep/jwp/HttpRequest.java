package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {


    private final Map<String, String> values = new HashMap<>();


    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean isStartLine = true;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            if (isStartLine) {
                String[] firstLine = line.split(" ");
                values.put("method", firstLine[0]);
                values.put("target", firstLine[1]);
                values.put("version", firstLine[2]);
                isStartLine = false;
            } else {
                String[] messages = line.split(":");
                values.put(messages[0], messages[1]);
            }
        }
    }

    public String getTarget() {
        return values.get("target");
    }
}
