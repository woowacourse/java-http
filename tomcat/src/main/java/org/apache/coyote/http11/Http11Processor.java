package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.utill.FileUtils;
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
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = generateHttpRequest(bufferedReader);

            if (httpRequest.getPath().equals("/")) {
                write(outputStream, HttpResponse.DEFAULT_HTTP_RESPONSE);
                return;
            }

            if (httpRequest.getPath().contains(".")) {
                String fileName = httpRequest.getPath().substring(1);
                write(outputStream, generate200HttpResponse(fileName));
                return;
            }

            if (httpRequest.getPath().equals("/login") && httpRequest.existsQueryString()) {
                validateExistsUser(httpRequest.getQueryParameter("account"), httpRequest.getQueryParameter("password"));
                write(outputStream, generate200HttpResponse("login.html"));
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(final BufferedReader bufferedReader) throws IOException {
        HttpRequest request = new HttpRequest(Objects.requireNonNull(bufferedReader.readLine()));
        log.info(request.getRequestLine());

        String line;
        while (!(line = Objects.requireNonNull(bufferedReader.readLine())).isBlank()) {
            String[] splitHeader = line.split(": ");
            request.addHeader(splitHeader[0], splitHeader[1]);
        }

        return request;
    }

    private HttpResponse generate200HttpResponse(final String fileName) throws IOException {
        String fileExtension = FileUtils.getFileExtension(fileName);
        ContentType contentType = ContentType.parse(fileExtension);

        String responseBody = FileUtils.readFile(fileName);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType.getValue());
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new HttpResponse("HTTP/1.1 200 OK", headers, responseBody);
    }

    private void write(final OutputStream outputStream, final HttpResponse httpResponse) throws IOException {
        outputStream.write(httpResponse.parseResponse().getBytes());
        outputStream.flush();
    }

    private void validateExistsUser(final String account, final String password) {
        if (!InMemoryUserRepository.existsAccountAndPassword(account, password)) {
            throw new NoSuchElementException();
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);
        log.info(user.toString());
    }
}
