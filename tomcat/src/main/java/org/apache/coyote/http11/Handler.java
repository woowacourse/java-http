package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.MemberAlreadyExistException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_HTML = "/login.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";

    public Response handle(final Request request) throws IOException {
        final RequestLine requestLine = request.getRequestLine();

        if (requestLine.getHttpMethod() == HttpMethod.GET && requestLine.getPath().startsWith("/login")) {
            if (isLoggedIn(request)) {
                final Session session = request.getSession(false);
                final User user = (User) session.getAttribute("user");
                log.info("user = {}", user);
                return redirect(INDEX_HTML);
            }
            return render(LOGIN_HTML);
        }

        if (requestLine.getHttpMethod() == HttpMethod.POST && requestLine.getPath().startsWith("/login")) {
            if (isLoggedIn(request)) {
                return redirect(INDEX_HTML);
            }
            final RequestBody requestBody = request.getRequestBody();
            final String account = requestBody.getParamValue("account");
            final String password = requestBody.getParamValue("password");
            final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);

            if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
                final User user = userOptional.get();
                log.info("user = {}", user);
                final Session session = request.getSession(true);
                session.setAttribute("user", user);
                return redirectWithSession(INDEX_HTML, session.getId());
            }
            return redirect(UNAUTHORIZED_HTML);
        }

        if (requestLine.getHttpMethod() == HttpMethod.POST && requestLine.getPath().startsWith("/register")) {
            final RequestBody requestBody = request.getRequestBody();
            final String accountValue = requestBody.getParamValue("account");
            InMemoryUserRepository.findByAccount(accountValue)
                    .ifPresentOrElse(
                            (member) -> new MemberAlreadyExistException(accountValue),
                            () -> InMemoryUserRepository.save(
                                    new User(
                                            requestBody.getParamValue("account"),
                                            requestBody.getParamValue("password"),
                                            requestBody.getParamValue("email")
                                    )
                            ));

            return redirect(INDEX_HTML);
        }

        if (requestLine.getHttpMethod() == HttpMethod.GET && !requestLine.getPath().equals("/")) {
            return render(requestLine.getPath());
        }

        return Response.of(HttpStatus.OK, ResponseBody.noContent(ContentType.HTML));
    }

    private boolean isLoggedIn(final Request request) {
        return request.hasSession() && SessionManager.has(request.getCookieValue("JSESSIONID"));
    }

    private Response render(final String path) throws IOException {
        final StaticResource staticResource = StaticResource.from(path);
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        return Response.of(HttpStatus.OK, responseBody);
    }

    private Response redirect(final String path) throws IOException {
        final StaticResource staticResource = StaticResource.from(path);
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        return Response.redirect(HttpStatus.FOUND, path, responseBody);
    }

    private Response redirectWithSession(final String path, final String sessionId) throws IOException {
        final Response response = redirect(path);
        response.addSession(sessionId);
        return response;
    }
}
