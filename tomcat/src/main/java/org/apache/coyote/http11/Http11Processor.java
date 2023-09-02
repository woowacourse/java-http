package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {

            final List<String> requestHeaderLines = new ArrayList<>();

            String nextLine;
            while (!"".equals(nextLine = bufferedReader.readLine())) {
                if (nextLine == null) {
                    return;
                }
                requestHeaderLines.add(nextLine);
            }

            Map<String, String> requestHeaders = new HashMap<>();
            for (int i = 1; i < requestHeaderLines.size(); i++) {
                String requestHeader = requestHeaderLines.get(i);
                String[] requestHeaderNameAndValue = requestHeader.split(":");

                String requestHeaderName = requestHeaderNameAndValue[0].trim().toLowerCase();
                String requestHeaderValue = requestHeaderNameAndValue[1].trim().toLowerCase();
                requestHeaders.put(requestHeaderName, requestHeaderValue);
            }

            final String requestFirstLine = requestHeaderLines.get(0);

            final String[] requestFirstLineElements = requestFirstLine.split(" ");
            final String requestMethod = requestFirstLineElements[0];
            final String requestUri = requestFirstLineElements[1];
            final String requestProtocol = requestFirstLineElements[2];

            log.info("method={}\r\nuri={}\r\nprotocol={}", requestMethod, requestUri, requestProtocol);

            String responseContentType = getResponseContentType(requestHeaders, requestUri);

            final String responseBody = getResponseBody(requestUri);

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + responseContentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseContentType(final Map<String, String> requestHeaders, final String requestUri) {
        String requestAcceptHeader = requestHeaders.getOrDefault("accept", "");
        String responseFileExtension = requestUri.substring(requestUri.indexOf(".") + 1);
        if ("text/css".equals(requestAcceptHeader) || "css".equals(responseFileExtension)) {
            return  "text/css,*/*;q=0.1";
        }
        if ("application/javascript".equals(requestAcceptHeader) || "js".equals(responseFileExtension)) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private String getResponseBody(final String requestUri) throws URISyntaxException, IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        final ClassLoader classLoader = getClass().getClassLoader();
        String name = "static" + requestUri;
        final URL fileURL = classLoader.getResource(name);

        if (fileURL == null) {
            throw new RuntimeException("404 NOT FOUND");
        }

        final URI fileURI = fileURL.toURI();

        final StringBuilder stringBuilder = new StringBuilder();
        try (final InputStream inputStream = new FileInputStream(Paths.get(fileURI).toFile());
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(nextLine)
                        .append(System.lineSeparator());
            }
        }

        return stringBuilder.toString();
    }
}
