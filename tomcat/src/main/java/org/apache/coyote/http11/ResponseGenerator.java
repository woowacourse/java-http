package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class ResponseGenerator {

    public static HttpResponse generate(final HttpRequest httpRequest) throws IOException {

        final String uri = httpRequest.getUri();
        final String version = httpRequest.getVersion();
        final String acceptLine = httpRequest.getAcceptLine();
        final String httpMethod = httpRequest.getMethod();
        final String requestBody = httpRequest.getBody();

        Cookie cookie = httpRequest.getCookie();

        if (uri.equals("/login")) {
            if (isLoggedIn(cookie)) {
                return new HttpResponse(version,"200 OK", MimeType.HTML.getMimeType(), "/index.html");
            }

            if (httpMethod.equals("GET")) {
                return new HttpResponse(version,"200 OK", MimeType.HTML.getMimeType(), "/login.html");
            }

            if (httpMethod.equals("POST")) {
                final String[] loginRequest = requestBody.split("&");
                final String account = loginRequest[0].split("=")[1];
                final String password = loginRequest[1].split("=")[1];

                final Optional<User> user = InMemoryUserRepository.findByAccount(account);
                if (isValidUser(user, password)) {
                    final UUID uuid = UUID.randomUUID();
                    final Session session = new Session(uuid.toString());
                    session.setAttribute("user", user);
                    final SessionManager sessionManager = SessionManager.getInstance();
                    sessionManager.add(session);

                    cookie = new Cookie("JSESSIONID=" + session.getId());
                    return new HttpResponse(version,"302 FOUND", MimeType.HTML.getMimeType(), "/index.html", cookie);
                }
                return new HttpResponse(version,"401 UNAUTHORIZED", MimeType.HTML.getMimeType(), "/401.html");
            }
        }

        if (uri.equals("/register")) {
            if (httpMethod.equals("GET")) {
                return new HttpResponse(version,"200 OK", MimeType.HTML.getMimeType(), "/register.html");
            }

            if (httpMethod.equals("POST")) {
                final String[] loginRequest = requestBody.split("&");
                final String account = loginRequest[0].split("=")[1];
                final String email = loginRequest[1].split("=")[1];
                final String password = loginRequest[2].split("=")[1];

                final User user = new User(account, email, password);
                InMemoryUserRepository.save(user);
                return new HttpResponse(version,"201 CREATED", MimeType.HTML.getMimeType(), "/index.html");
            }
        }

        if (MimeType.isValidType(uri)) {
            final String fileExtension = resolveFileExtension(uri);
            final String contentType = resolveContentType(fileExtension, acceptLine);
            return new HttpResponse(version,"200 OK", contentType, uri);
        }

        return new HttpResponse(version,"404 NOT FOUND", MimeType.HTML.getMimeType(), "/404.html");
    }

    private static boolean isValidUser(Optional<User> user, String password) {
        if (user.isEmpty()) {
            System.out.println("일치하는 유저가 없습니다.");
            return false;
        }
        final boolean isMatchedPassword = user.get().checkPassword(password);
        if (isMatchedPassword) {
            return true;
        }
        System.out.println("비밀번호가 틀렸습니다.");
        return false;
    }

    private static boolean isLoggedIn(final Cookie cookie) {
        if (cookie == null) {
            return false;
        }
        if (cookie.isJSessionCookie()) {
            return SessionManager.getInstance().hasSession(cookie.getJSessionId());
        }
        return false;
    }

    private static String resolveFileExtension(final String resourceUri) {
        final int lastDotIndex = resourceUri.lastIndexOf(".");
        return resourceUri.substring(lastDotIndex + 1);
    }

    private static String resolveContentType(final String fileExtension, final String acceptLine) {
        if (acceptLine != null && acceptLine.contains(fileExtension)) {
            return MimeType.toMimeType(fileExtension);
        }
        return "*/*";
    }
}
