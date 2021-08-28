package nextstep.jwp.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private static final String REQUEST_SEPARATOR = " ";

    final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public ClientRequest extractClientRequest() throws IOException {
        final String request = bufferedReader.readLine();
        log.info("client request = {}", request);
        final String[] requestInfos = request.split(REQUEST_SEPARATOR);
        return new ClientRequest(HttpMethod.of(requestInfos[0]), RequestUrl.of(requestInfos[1]));
    }
}
