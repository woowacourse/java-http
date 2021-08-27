package nextstep.jwp.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    private final String path;

    public Request(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        logger.debug(line);
        String[] firstLine = line.split(" ");
        this.path = firstLine[1];

        while (!"".equals(line)) {
            if (line == null) {
                return;
            }
            line = reader.readLine();
            logger.debug(line);
        }
    }

    public String getPath() {
        return path;
    }
}
