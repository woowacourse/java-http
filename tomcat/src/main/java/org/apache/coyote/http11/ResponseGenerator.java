package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class ResponseGenerator {

    public static String generate(final String uri, final String acceptLine) throws IOException {
        if (isLoginUri(uri)) {
            return generateLoginResponse(uri, acceptLine);
        }
        try {
            return generateStaticResourceResponse(uri, acceptLine);
        } catch (final NullPointerException e) {
            return notFoundResponse();
        }
    }

    private static boolean isLoginUri(final String uri) {
        return uri.startsWith("/login");
    }

    private static String generateLoginResponse(final String uri, final String acceptLine) throws IOException {
        if (uri.equals("/login")) {
            return generateStaticResourceResponse(uri + ".html", acceptLine);
        }
        return generateLoginResultResponse(uri);
    }

    private static String generateStaticResourceResponse(final String resourceUri, final String acceptLine) throws IOException {
        final InputStream staticResource = ResponseGenerator.class.getClassLoader().getResourceAsStream("static" + resourceUri);
        final byte[] bytes = staticResource.readAllBytes();
        final String resource = new String(bytes);

        final String fileExtension = resolveFileExtension(resourceUri);
        final String contentType = resolveContentType(fileExtension, acceptLine);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
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

    private static String generateLoginResultResponse(final String uri) {
        final int queryStartIndex = uri.indexOf("?") + 1;
        final String queryString = uri.substring(queryStartIndex);
        final String authenticateRequestResult = authenticate(queryString);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + authenticateRequestResult.getBytes().length + " ",
                "",
                authenticateRequestResult);
    }

    private static String authenticate(final String queryString) {
        final String[] loginRequest = queryString.split("&");
        final String[] accountPair = loginRequest[0].split("=");
        final String accountRequest = accountPair[1];
        final String[] passwordPair = loginRequest[1].split("=");
        final String passwordRequest = passwordPair[1];

        final Optional<User> user = InMemoryUserRepository.findByAccount(accountRequest);
        if (user.isEmpty()) {
            return "일치하는 유저가 없습니다.";
        }
        final boolean isMatchedPassword = user.get().checkPassword(passwordRequest);

        if (isMatchedPassword) {
            return  "로그인 성공";
        }
        return "비밀번호가 틀렸습니다.";
    }

    private static String notFoundResponse() throws IOException {
        InputStream resourceAsStream = ResponseGenerator.class.getClassLoader()
                .getResourceAsStream("static" + "/assets/img/error-404-monochrome.svg");
        String resource = new String(resourceAsStream.readAllBytes());

        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Content-Type: image/svg+xml;charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
    }
}
