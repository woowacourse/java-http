package nextstep.jwp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceReader {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceReader.class);

    public StaticResourceReader() {
    }

    public String content(String targetResource) throws IOException {
        log.debug("Target Resource >> {}", targetResource);

        InputStream resource =
            getClass().getResourceAsStream("/static" + targetResource);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource));

        StringBuilder sb = new StringBuilder();
        while (bufferedReader.ready()) {
            sb.append(bufferedReader.readLine());
            sb.append("\n");
        }

        return sb.toString();
    }
}
