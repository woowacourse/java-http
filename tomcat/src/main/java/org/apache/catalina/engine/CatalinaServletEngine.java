package org.apache.catalina.engine;

import static org.apache.coyote.http11.RequestLine.REQUEST_URI;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class CatalinaServletEngine {

    private static final Logger log = LoggerFactory.getLogger(CatalinaServletEngine.class);

    public static void processRequest(Map<RequestLine, String> requestLine, Map<String, String> requestHeaders, StringBuilder response) {
        String contentType = convertContentTypeByAccept(requestHeaders.get("Accept"));
        if (requestLine.get(REQUEST_URI).equals("/")) {
            String content = findStaticFile("/index.html");
            response(response, content, contentType);
            return;
        }
        if (requestLine.get(REQUEST_URI).startsWith("/login")) {
            parseLogin(requestLine.get(RequestLine.REQUEST_URI));
            String content = findStaticFile("/login.html");
            response(response, content, contentType);
            return;
        }
        String content = findStaticFile(requestLine.get(REQUEST_URI));
        if (!StringUtils.isEmpty(content)) {
            response(response, content, contentType);
        }
    }

    private static void response(StringBuilder response, String content, String contentType) {
        response.append("HTTP/1.1 200 OK ")
                .append("\r\n" + "Content-Type: " + contentType + ";charset=utf-8 ")
                .append("\r\n" + "Content-Length: " + content.getBytes().length + " " + "\r\n")
                .append("\r\n" + content);
    }

    private static String findStaticFile(String filename) {
        try {
            URL resource = CatalinaServletEngine.class.getClassLoader()
                    .getResource("static" + filename);
            if (Objects.isNull(resource)) {
                return StringUtils.EMPTY;
            }
            return new String(Files.readAllBytes(new File((resource).getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertContentTypeByAccept(String accept) {
        if (!Objects.isNull(accept) && accept.contains("text/css")) {
            return "text/css";
        }
        return "text/html";
    }

    private static void parseLogin(String uri) {
        if (!(uri.contains("?account=") && uri.contains("&password="))) {
            return;
        }
        String queryString = uri.substring("/login?".length());
        int index = queryString.indexOf("&");
        String account = queryString.substring("account=".length(), index);
        String password = queryString.substring(index + 1 + "password=".length());
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> {
                            log.error("account: {} 는 존재하지 않는 사용자입니다.", account);
                            return new RuntimeException();
                        }
                );
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            return;
        }
        log.error("user: {}, inputPassword={}, 비밀번호가 올바르지 않습니다.", user, password);
        throw new RuntimeException();
    }
}
