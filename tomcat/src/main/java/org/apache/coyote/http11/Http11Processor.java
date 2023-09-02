package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.ResponseGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int REQUEST_URI_MESSAGE_INDEX = 1;

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String startLine = br.readLine();
            String[] startLineElements = startLine.split(" ");
            String requestUri = startLineElements[REQUEST_URI_MESSAGE_INDEX];
            RequestUri requestUriVo = new RequestUri(requestUri);

            String response = "";
            if (!requestUri.endsWith(".ico")) {
                ResponseGenerator responseGenerator = new ResponseGenerator(requestUriVo);
                response = responseGenerator.generateSuccessResponse();
                if (requestUri.startsWith("/login")) {
                    Map<String, String> loginQueryParams = requestUriVo.parseQueryParams();

                    User findUser = InMemoryUserRepository.findByAccount(loginQueryParams.get("account")).get();
                    log.info("user : {}", findUser);
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
