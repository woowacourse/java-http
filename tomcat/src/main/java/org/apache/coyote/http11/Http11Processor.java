package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestParameters;
import org.apache.coyote.http11.request.Session;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import static org.apache.coyote.http11.request.RequestMethod.GET;
import static org.apache.coyote.http11.request.RequestMethod.POST;
import static org.apache.coyote.http11.response.Response.getUnauthorizedResponse;

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

            response.decideHeaders(request);

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
        final RequestParameters requestParameters = request.getRequestParameters();

        if (request.isMatching("/", GET)) {
            return new Response("Hello world!");
        }

        if (request.isMatching("/login", GET)) {
            final Session session = request.getSession();
            final User user = (User) session.getAttribute("user");
            if (user == null) {
                // TODO: 9/6/23 GET POST 나누고 이 부분 login으로 리다이렉토로 변경
                return Response.getRedirectResponse("index.html");
            }
            final String account = requestParameters.getValue("account");
            if (account == null) {
                // TODO: 9/6/23 /login으로 변경 필요
                return findStaticResource("/login.html");
            }

            final Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
            if (maybeUser.isEmpty()) {
                return getUnauthorizedResponse();
            }
            final User findUser = maybeUser.get();
            if (!findUser.checkPassword(requestParameters.getValue("password"))) {
                return getUnauthorizedResponse();
            }
            log.info("user: {}", findUser);

            session.setAttribute("user", findUser);

            return Response.getRedirectResponse("index.html");
        }

        if (request.isMatching("/register", GET)) {
            return findStaticResource("/register.html");
        }

        if (request.isMatching("/register", POST)) {
            final String account = requestParameters.getValue("account");
            final String password = requestParameters.getValue("password");
            final String email = requestParameters.getValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return Response.getRedirectResponse("login");
        }

        return Response.getNotFoundResponse();
    }

    private Response findStaticResource(final String requestUri) throws IOException, URISyntaxException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String name = "static" + requestUri;
        final URL fileURL = classLoader.getResource(name);

        if (fileURL == null) {
            return Response.getNotFoundResponse();
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
