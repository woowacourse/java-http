package nextstep.jwp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceReader {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceReader.class);

    private String resource;

    private StaticResourceReader() {
    }

    public StaticResourceReader(String resource) {
        this.resource = resource;
    }

    public String content() throws IOException {
        log.debug("Target Resource: {}", resource);

        InputStream resourceAsStream =
            getClass().getResourceAsStream("/static" + resource);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        StringBuilder sb = new StringBuilder();

        try (
            resourceAsStream;
            bufferedReader
        ) {
            while (bufferedReader.ready()) {
                sb.append(bufferedReader.readLine());
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
