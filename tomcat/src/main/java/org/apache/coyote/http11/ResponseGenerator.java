package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class ResponseGenerator {

    private static boolean IS_LOGINED = false;
    private static User LOGINED_USER = null;

    public static String generate(final HttpRequest httpRequest, final BufferedReader bufferedReader) throws IOException {
        final String uri = httpRequest.getUri();
        final String acceptLine = httpRequest.getAcceptLine();
        final String httpMethod = httpRequest.getMethod();
        final String contentLength = httpRequest.getContentLength();
        final boolean isCookieExist = httpRequest.hasCookie();

        if (isLoginUri(uri)) {
            if (IS_LOGINED) {
                return generateStaticResourceResponse("/index.html", acceptLine, "200 OK", isCookieExist);
            }
            return generateLoginResponse(uri, acceptLine, httpMethod, contentLength, bufferedReader, isCookieExist);
        }
        if (isRegisterUri(uri)) {
            if (httpMethod.equals("POST")) {
                final int length = Integer.parseInt(contentLength);
                char[] buffer = new char[length];
                bufferedReader.read(buffer, 0, length);
                String requestBody = new String(buffer);

                Map<String, String> registerRequest = new HashMap<>();
                for (String pair : requestBody.split("&")) {
                    final String[] request = pair.split("=");
                    registerRequest.put(request[0], request[1]);
                }

                final User user = new User(registerRequest.get("account"), registerRequest.get("password"), registerRequest.get("email"));
                InMemoryUserRepository.save(user);
                LOGINED_USER = user;

                return generateStaticResourceResponse("/index.html", acceptLine, "201 OK", isCookieExist);
            }

            return generateRegisterResponse(uri, acceptLine, isCookieExist);
        }
        try {
            return generateStaticResourceResponse(uri, acceptLine, "200 OK", isCookieExist);
        } catch (final NullPointerException e) {
            return notFoundResponse();
        }
    }

    private static boolean isLoginUri(final String uri) {
        return uri.equals("/login");
    }

    private static String generateLoginResponse(final String uri, final String acceptLine, final String httpMethod, final String contentLength, final BufferedReader bufferedReader, final boolean isCookieExist) throws IOException {
        if (httpMethod.equals("GET")) {
            return generateStaticResourceResponse(uri + ".html", acceptLine, "200 OK", isCookieExist);
        }

            final int length = Integer.parseInt(contentLength);
            char[] buffer = new char[length];
            bufferedReader.read(buffer, 0, length);
            String requestBody = new String(buffer);

            Map<String, String> registerRequest = new HashMap<>();
            for (String pair : requestBody.split("&")) {
                final String[] request = pair.split("=");
                registerRequest.put(request[0], request[1]);
            }

            if (isValidUser(registerRequest)) {
                System.out.println("가능유저");
                IS_LOGINED = true;
                return generateStaticResourceResponse("/index.html", MimeType.HTML.getMimeType(), "302 FOUND", isCookieExist);
            }
        System.out.println("불가능유저");
            return generateStaticResourceResponse("/401.html", MimeType.HTML.getMimeType(), "401 UNAUTHORIZED", isCookieExist);
    }


    private static String generateStaticResourceResponse(final String resourceUri, final String acceptLine, final String httpStatusCode, boolean isCookieExist) throws IOException {
        final InputStream staticResource = ResponseGenerator.class.getClassLoader().getResourceAsStream("static" + resourceUri);
        final byte[] bytes = staticResource.readAllBytes();
        final String resource = new String(bytes);

        final String fileExtension = resolveFileExtension(resourceUri);
        final String contentType = resolveContentType(fileExtension, acceptLine);

        if (isCookieExist) {
            return makeResponse(httpStatusCode, contentType, resource);
        }
        if (IS_LOGINED) {
            final String randomId = String.valueOf(UUID.randomUUID());
            final Session session = new Session(randomId);
            session.setAttribute("user", LOGINED_USER);

            Cookie cookie = new Cookie(session.getId());
            final String jsessionid = cookie.getJsessionid();
            return makeCookieResponse(httpStatusCode, jsessionid, contentType, resource);
        }
        return makeRemovedCookieResponse(httpStatusCode, "", contentType, resource);
    }

    private static String resolveFileExtension(final String resourceUri) {
        final int lastDotIndex = resourceUri.lastIndexOf(".");
        return resourceUri.substring(lastDotIndex + 1);
    }

    private static String resolveContentType(final String fileExtension, final String acceptLine) {
        if (acceptLine.contains(fileExtension)) {
            return MimeType.toMimeType(fileExtension);
        }
        return "*/*";
    }

    private static boolean isValidUser(Map<String, String> registerRequest) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(registerRequest.get("account"));
        if (user.isEmpty()) {
            System.out.println("일치하는 유저가 없습니다.");
            return false;
        }
        final boolean isMatchedPassword = user.get().checkPassword(registerRequest.get("password"));
        if (isMatchedPassword) {
            return true;
        }
        System.out.println("비밀번호가 틀렸습니다.");
        return false;
    }

    private static boolean isRegisterUri(final String uri) {
        return uri.equals("/register");
    }
    private static String generateRegisterResponse(final String uri, final String acceptLine, final boolean isCookieExist) throws IOException {

        return generateStaticResourceResponse(uri + ".html", acceptLine, "200 OK", isCookieExist);
    }

    private static String notFoundResponse() throws IOException {
        InputStream resourceAsStream = ResponseGenerator.class.getClassLoader()
                .getResourceAsStream("static" + "/assets/img/error-404-monochrome.svg");
        String resource = new String(resourceAsStream.readAllBytes());

        return makeResponse("404 NOT FOUND", MimeType.SVG.getMimeType(), resource);
    }

    private static String makeCookieResponse(String httpStatusCode, String jsessionid, String contentType, String resource) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode + " ",
                "Set-Cookie: " + jsessionid,
                "Content-Type: "+contentType+";charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
    }

    private static String makeResponse(String httpStatusCode, String contentType, String resource) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode + " ",
                "Content-Type: "+contentType+";charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
    }

    private static String makeRemovedCookieResponse(final String httpStatusCode, final String emptyCookie, final String contentType, final String resource) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode + " ",
                "Set-Cookie: " + emptyCookie,
                "Content-Type: "+contentType+";charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
    }
}
