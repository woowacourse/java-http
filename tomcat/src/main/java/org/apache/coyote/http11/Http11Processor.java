package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestParameters;
import org.apache.coyote.http11.request.Session;
import org.apache.coyote.http11.request.SessionManager;
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
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.http11.header.ResponseHeader.LOCATION;
import static org.apache.coyote.http11.header.ResponseHeader.SET_COOKIE;
import static org.apache.coyote.http11.request.RequestMethod.GET;
import static org.apache.coyote.http11.request.RequestMethod.POST;
import static org.apache.coyote.http11.response.Response.UNAUTHORIZED_RESPONSE;

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

        if ("/".equals(requestPath) && request.getRequestLine().getRequestMethod() == GET) {
            return new Response("Hello world!");
        }

        if ("/login".equals(requestPath) && request.getRequestLine().getRequestMethod() == GET) {
            final String jSessionIdName = "JSESSIONID";
            final String userSessionKey = "user";
            if (request.getHttpCookie().existCookie(jSessionIdName)) {
                final String jSessionIdValue = request.getHttpCookie().findCookie(jSessionIdName);
                if (!SessionManager.existSession(jSessionIdValue)) {
                    final Session session = new Session(jSessionIdValue);
                    SessionManager.add(session);
                }
                final Session session = SessionManager.findSession(jSessionIdValue);
                final User user = (User) session.getAttribute(userSessionKey);
                if (user == null) {
                    final Headers headers = new Headers();
                    // TODO: 9/4/23 쿠키 삭제하는 작업 추가해야 함
                    headers.addHeader(LOCATION, "/index.html");
                    return new Response(new StatusLine(StatusCode.FOUND), headers, "");
                }
                final Headers headers = new Headers();
                headers.addHeader(LOCATION, "/index.html");
                return new Response(new StatusLine(StatusCode.FOUND), headers, "");
            }

            final String account = requestParameters.getValue("account");
            if (account == null) {
                return findStaticResource("/login.html");
            }
            final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
            if (maybeUser.isEmpty()) {
                return UNAUTHORIZED_RESPONSE;
            }
            final User user = maybeUser.get();
            if (!user.checkPassword(requestParameters.getValue("password"))) {
                return UNAUTHORIZED_RESPONSE;
            }
            log.info("user: {}", user);

            final Headers headers = new Headers();
            headers.addHeader(LOCATION, "/index.html");

            final Session session = getSession(request, headers);
            session.setAttribute(userSessionKey, user);

            return new Response(new StatusLine(StatusCode.FOUND), headers, "");
        }

        if ("/register".equals(requestPath) && request.getRequestLine().getRequestMethod() == GET) {
            return findStaticResource("/register.html");
        }

        if ("/register".equals(requestPath) && request.getRequestLine().getRequestMethod() == POST) {
            final String account = requestParameters.getValue("account");
            final String password = requestParameters.getValue("password");
            final String email = requestParameters.getValue("email");

            final User user = new User(account, password, email);

            InMemoryUserRepository.save(user);

            final Headers headers = new Headers();
            headers.addHeader(LOCATION, "/login");
            return new Response(new StatusLine(StatusCode.FOUND), headers, "");
        }

        return Response.NOT_FOUND_RESPONSE;
    }

    private Session getSession(final Request request, final Headers headers) {
        final String jSessionIdName = "JSESSIONID";
        if (!request.getHttpCookie().existCookie(jSessionIdName)) {
            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            SessionManager.add(session);
            headers.addHeader(SET_COOKIE, jSessionIdName + "=" + sessionId);
            return session;
        }
        final String name = request.getHttpCookie().findCookie(jSessionIdName);
        return SessionManager.findSession(request.getHttpCookie().findCookie(name));
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
