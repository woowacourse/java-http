package nextstep.jwp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    private String method;
    private String path;
    private String body = "";
    private Map<String, String> headers = new HashMap<>();

    public Request(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        logger.debug(line);
        String[] firstLine = line.split(" ");
        this.method = firstLine[0];
        this.path = firstLine[1];

        while (!"".equals(line)) {
            if (line == null) {
                return;
            }
            line = reader.readLine();
            logger.debug(line);
            String[] parsedLine = line.split(": ");
            if (parsedLine.length < 2) {
                break;
            }
            headers.put(parsedLine[0], parsedLine[1]);
        }

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length").trim());
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }

        logger.debug(body);
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public String getBody() {
        return body;
    }
}
