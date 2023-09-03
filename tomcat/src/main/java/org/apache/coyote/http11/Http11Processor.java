package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusLine;
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

            final String requestFirstLine = requestHeaderLines.get(0);

            final String[] requestFirstLineElements = requestFirstLine.split(" ");
            final String requestMethod = requestFirstLineElements[0];
            final String requestUrl = requestFirstLineElements[1];
            final String requestProtocol = requestFirstLineElements[2];

            final Map<String, String> requestHeaders = getRequestHeaders(requestHeaderLines);

            String requestUri = requestUrl;

            Map<String, String> requestQueryParameter = new HashMap<>();

            if (requestUrl.contains("?")) {
                requestUri = requestUrl.substring(0, requestUrl.indexOf("?"));
                requestQueryParameter = getQueryString(requestUrl);
            }

            log.info("method={}\r\nuri={}\r\nprotocol={}", requestMethod, requestUri, requestProtocol);
            log.info("queryString={}", requestQueryParameter);

            final Response response = getResponse(requestUri, requestHeaders, requestQueryParameter);
            final String requestAcceptHeader = requestHeaders.getOrDefault("accept", "");
            response.decideContentType(requestAcceptHeader, requestUri);
            response.decideContentLength();

            outputStream.write(response.parseString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getRequestHeaders(final List<String> requestHeaderLines) {
        final Map<String, String> requestHeaders = new HashMap<>();
        for (int i = 1; i < requestHeaderLines.size(); i++) {
            final String requestHeader = requestHeaderLines.get(i);
            final String[] requestHeaderNameAndValue = requestHeader.split(":");
            final String requestHeaderName = requestHeaderNameAndValue[0].trim().toLowerCase();
            final String requestHeaderValue = requestHeaderNameAndValue[1].trim().toLowerCase();
            requestHeaders.put(requestHeaderName, requestHeaderValue);
        }
        return requestHeaders;
    }

    private Map<String, String> getQueryString(final String requestUri) {
        final Map<String, String> requestQueryParameters = new HashMap<>();
        final String queryStrings = requestUri.substring(requestUri.indexOf("?") + 1);
        final String[] queryStringsNameAndValue = queryStrings.split("&");
        for (String queryString : queryStringsNameAndValue) {
            final String[] queryStringNameAndValue = queryString.split("=");
            final String name = queryStringNameAndValue[0];
            final String value = queryStringNameAndValue[1];
            requestQueryParameters.put(name, value);
        }
        return requestQueryParameters;
    }

    private Response getResponse(final String requestUri,
                               final Map<String, String> requestHeaders,
                               final Map<String, String> requestParameter) throws URISyntaxException, IOException {
        if (requestUri.contains(".")) {
            return findStaticResource(requestUri, requestHeaders, requestParameter);
        }
        return mapPath(requestUri, requestHeaders, requestParameter);
    }

    private Response mapPath(final String requestUri,
                           final Map<String, String> requestHeaders,
                           final Map<String, String> requestParameter) throws IOException, URISyntaxException {
        if (requestUri.equals("/")) {
            return new Response("Hello world!");
        }
        if ("/login".equals(requestUri)) {
            final String account = requestParameter.get("account");
            if (account == null) {
                return findStaticResource("/login.html", requestHeaders, requestParameter);
            }
            final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
            if (maybeUser.isEmpty()) {
                return Response.UNAUTHORIZED_RESPONSE;
            }
            final User user = maybeUser.get();
            if (!user.checkPassword(requestParameter.get("password"))) {
                return Response.UNAUTHORIZED_RESPONSE;
            }
            log.info("user: {}", user);

            final Headers headers = new Headers();
            headers.addHeader(ResponseHeader.LOCATION, "/index.html");
            return new Response(new StatusLine(StatusCode.FOUND), headers, "");
        }
        return Response.NOT_FOUND_RESPONSE;
    }

    private Response findStaticResource(final String requestUri,
                                      final Map<String, String> requestHeaders,
                                      final Map<String, String> requestParameter) throws IOException, URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String name = "static" + requestUri;
        final URL fileURL = classLoader.getResource(name);

        if (fileURL == null) {
            return Response.NOT_FOUND_RESPONSE;
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

        return new Response(stringBuilder.toString());
    }
}
