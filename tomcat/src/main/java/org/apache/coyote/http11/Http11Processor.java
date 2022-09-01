package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequestMessage httpRequestMessage = generateHttpRequestMessage(bufferedReader);
            String requestTarget = httpRequestMessage.getRequestTarget();

            if (requestTarget.equals("/")) {
                HttpResponseMessage httpResponseMessage = HttpResponseMessage.DEFAULT_HTTP_RESPONSE;
                outputStream.write(httpResponseMessage.parseResponse().getBytes());
                outputStream.flush();
                return;
            }

            if (!requestTarget.contains(".") && requestTarget.startsWith("/login")
                    && requestTarget.contains("?") && requestTarget.contains("=")) {
                int index = requestTarget.indexOf("?");
                QueryParameters queryParameters = new QueryParameters(requestTarget.substring(index + 1));

                HttpResponseMessage httpResponseMessage = generateResponseMessage("login.html");
                String response = httpResponseMessage.parseResponse();

                User user = InMemoryUserRepository.findByAccount(queryParameters.getParameterValue("account"))
                        .orElseThrow(NoSuchElementException::new);
                log.info(user.toString());

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            String fileName = requestTarget.substring(1);

            HttpResponseMessage httpResponseMessage = generateResponseMessage(fileName);
            String response = httpResponseMessage.parseResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestMessage generateHttpRequestMessage(final BufferedReader bufferedReader) throws IOException {
        HttpRequestMessage requestMessage = new HttpRequestMessage(Objects.requireNonNull(bufferedReader.readLine()));
        log.info(requestMessage.getRequestLine());

        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            String[] splitHeader = line.split(": ");
            requestMessage.addHeader(splitHeader[0], splitHeader[1]);
        }

        return requestMessage;
    }

    private HttpResponseMessage generateResponseMessage(final String fileName) throws IOException {
        String statusLine = "HTTP/1.1 200 OK";

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        URL resourceUrl = getClass().getClassLoader().getResource("static/" + fileName);
        String responseBody = new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));

        String contentType = ContentType.parse(fileName.split("\\.")[1])
                .getValue();

        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new HttpResponseMessage(statusLine, headers, responseBody);
    }
}
