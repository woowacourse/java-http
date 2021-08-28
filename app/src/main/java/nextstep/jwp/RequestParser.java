package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {
    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);

    final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public ClientRequest parse() throws IOException {
        final String requestFirstLine = bufferedReader.readLine();
        log.info("request First Line is = {}", requestFirstLine);
        return ClientRequest.of(requestFirstLine);
    }
}
