package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestParameters;
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
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = connection.getOutputStream()) {

            final Request request = Request.from(bufferedReader);

            final Response response = handle(request);
            response.decideContentType(request);
            response.decideContentLength();

            outputStream.write(response.parseString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response handle(final Request request) throws URISyntaxException, IOException {
        final String requestPath = request.getRequestLine().getRequestPath();
        if (requestPath.contains(".")) {
            return findStaticResource(requestPath);
        }
        return mapPath(request);
    }

    private Response mapPath(final Request request) throws IOException, URISyntaxException {

        final String requestPath = request.getRequestLine().getRequestPath();
        final RequestParameters requestParameters = request.getRequestParameters();

        if ("/".equals(requestPath)) {
            return new Response("Hello world!");
        }

        if ("/login".equals(requestPath)) {
            final String account = requestParameters.getValue("account");
            if (account == null) {
                return findStaticResource("/login.html");
            }
            final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
            if (maybeUser.isEmpty()) {
                return Response.UNAUTHORIZED_RESPONSE;
            }
            final User user = maybeUser.get();
            if (!user.checkPassword(requestParameters.getValue("password"))) {
                return Response.UNAUTHORIZED_RESPONSE;
            }
            log.info("user: {}", user);
            final Headers headers = new Headers();
            headers.addHeader(ResponseHeader.LOCATION, "/index.html");
            return new Response(new StatusLine(StatusCode.FOUND), headers, "");
        }

        return Response.NOT_FOUND_RESPONSE;
    }

    private Response findStaticResource(final String requestUri) throws IOException, URISyntaxException {
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
