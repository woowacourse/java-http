package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String REQUEST_URI = "REQUEST URI";
    private static final String HTTP_METHOD = "HTTP METHOD";
    private static final String HTTP_VERSION = "HTTP VERSION";

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
             final var outputStream = connection.getOutputStream()) {

            Map<String, String> requestHeaders = getRequestHeaders(inputStream);

            URI requestUri = new URI("http://" + requestHeaders.get("Host") + requestHeaders.get(REQUEST_URI));
            URL requestUrl = getRequestUrl(requestUri);
            Map<String, List<String>> queryParams = getQueryParams(requestUri);

            if (!queryParams.isEmpty() && queryParams.containsKey("account") && queryParams.containsKey("password")) {
                User user = InMemoryUserRepository.findByAccount(queryParams.get("account").get(0))
                    .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));
                if (!user.checkPassword(queryParams.get("password").get(0))) {
                    throw new IllegalArgumentException("Password Not Matched");
                }
                log.info("user : {}", user);
            }

            final var responseBody = getResponseBody(requestUrl);

            final var response = getResponse(requestHeaders, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(Map<String, String> requestHeaders, String responseBody) {
        String contentType = "Content-Type: text/html;charset=utf-8 ";
        if (requestHeaders.containsKey("Accept") && requestHeaders.get("Accept").contains("text/css")) {
            contentType = "Content-Type: text/css;";
        }

        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            contentType
            ,
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private Map<String, String> getRequestHeaders(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = bufferedReader.readLine();
        Map<String, String> requestHeaders = new HashMap<>();

        try {
            while (!"".equals(line)) {
                if (Objects.isNull(line)) {
                    return Map.of();
                }
                putHeader(requestHeaders, line);
                line = bufferedReader.readLine();
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("invalid HTTP request received", e.getCause());
        }
        return requestHeaders;
    }

    private void putHeader(Map<String, String> requestHeaders, String requestLine) {
        if (!requestHeaders.isEmpty()) {
            List<String> headerAndValue = parseRequestLine(requestLine, ":");
            requestHeaders.put(headerAndValue.get(0), headerAndValue.get(1));
            return;
        }
        List<String> startLine = parseRequestLine(requestLine, " ");
        requestHeaders.put(HTTP_METHOD, startLine.get(0));
        requestHeaders.put(REQUEST_URI, startLine.get(1));
        requestHeaders.put(HTTP_VERSION, startLine.get(2));
    }

    private List<String> parseRequestLine(String requestLine, String delimiter) {
        return Arrays.stream(requestLine.split(delimiter))
            .map(String::trim)
            .collect(toList());
    }

    private String getResponseBody(URL requestUrl) throws IOException {
        if (requestUrl.getPath().equals("/")) {
            return "Hello world!";
        }

        return new String(Files.readAllBytes(new File(Objects.requireNonNull(requestUrl).getFile()).toPath()));
    }

    private Map<String, List<String>> getQueryParams(URI resource) {
        if (Objects.isNull(resource)) {
            return Collections.emptyMap();
        }

        String query = resource.getQuery();
        if (Objects.isNull(query) || query.isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(query.split("&"))
            .map(this::splitQueryParameters)
            .collect(Collectors.groupingBy(
                AbstractMap.SimpleImmutableEntry::getKey, HashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameters(String query) {
        int index = query.indexOf("=");
        String key = query;
        String value = "";
        if (index > 0) {
            key = key.substring(0, index);
        }

        if (index > 0 && query.length() > index + 1) {
            value = query.substring(index + 1);
        }

        return new AbstractMap.SimpleImmutableEntry<>(
            URLDecoder.decode(key, StandardCharsets.UTF_8),
            URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }

    private URL getRequestUrl(URI requestURI) throws MalformedURLException, URISyntaxException {
        if (requestURI.getPath().equals("/")) {
            return requestURI.toURL();
        }
        if (!Objects.isNull(requestURI.getQuery())) {
            return getRequestUrl(new URI(
                requestURI.getScheme() + "://" + requestURI.getHost() + ":" + requestURI.getPort()
                    + requestURI.getPath()));
        }

        if (!requestURI.getPath().contains(".")) {
            requestURI = new URI(requestURI + ".html");
        }

        URL resource = getClass().getClassLoader().getResource("static" + requestURI.getPath());
        if (Objects.isNull(resource)) {
            return requestURI.toURL();
        }
        return resource;
    }
}
