package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final StartLine startLine = StartLine.of(bufferedReader.readLine());
            final Map<String, String> queryParams = startLine.getQueryParams();

            if (queryParams.containsKey("account")) {
                LoginService.checkAccount(queryParams);
            }

            final ContentType contentType = ContentType.from(startLine.getRequestUri());
            final String responseBody = getResponseBody(startLine.getRequestUri());
            final HttpResponse httpResponse = HttpResponse.builder()
                    .contentType(contentType)
                    .responseBody(responseBody)
                    .build();

            outputStream.write(httpResponse.getValue().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            requestUri = "/index.html";
        }
        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }
        return FileUtil.readAllBytes(requestUri);
    }
}
