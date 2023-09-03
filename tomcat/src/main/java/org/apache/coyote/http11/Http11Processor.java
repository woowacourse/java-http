package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.Processor;
import org.apache.coyote.Sessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";
    private static final String SPACE = " ";
    private static final String MULTIPLE_QUERY_STRING_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String LINE_FEED = "\r\n";
    private static final String HTML_SUFFIX = ".html";
    private static List<String> STATIC_PATH = List.of(".css", ".js");

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpRequestParser = new HttpRequestParser();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            httpRequestParser.accept(inputStream);
            final String response = createResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse() throws IOException {
        String path = httpRequestParser.findPath();
        String method = httpRequestParser.findMethod();
        String prevPath = path;
        Map<String, String> cookies = httpRequestParser.findCookies();
        String jsessionid = cookies.get("JSESSIONID");
        if (method.equals("GET")) {
            path = processGetRequest(path, jsessionid);
        } else if (method.equals("POST")) {
            if (path.equals("/login")) {
                path = processLogin();
            } else if (path.equals("/register")) {
                path = processRegister();
            }
        }
        String status = getStatus(prevPath, path);

        String protocol = httpRequestParser.findProtocol();
        String contentType = getContentType(path);

        String content = getContent(path);
        String contentLength = "Content-Length: " + content.getBytes().length;

        String response = protocol +  SPACE + status + SPACE + LINE_FEED +
                getJSessionId() + SPACE + LINE_FEED +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                getLocationIfRedirect(status, path) +
                LINE_FEED +
                content;
        return response;
    }

    private String getJSessionId() {
        Map<String, String> cookies = httpRequestParser.findCookies();
        if (!cookies.containsKey("JSESSIONID")) {
            return "Set-Cookie: JSESSIONID=" + UUID.randomUUID().toString();
        }
        return "";
    }

    private String processLogin() {
        String path;
        String[] splitRequestBody = httpRequestParser.getMessageBody().split(MULTIPLE_QUERY_STRING_SEPARATOR);
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String password = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        try {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
            addSession(user);
            path = getRedirectPath(password, user);
            log.info(user.toString());
        } catch (UserNotFoundException e) {
            path =  "/401.html";
        }
        return path;
    }

    private void addSession(User user) {
        Sessions sessions = new Sessions();
        Map<String, String> cookies = httpRequestParser.findCookies();
        String jsessionid = cookies.get("JSESSIONID");
        sessions.add(jsessionid, user);
    }

    private String processRegister() {
        String[] splitRequestBody = httpRequestParser.getMessageBody().split(MULTIPLE_QUERY_STRING_SEPARATOR);
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String email = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        email = email.replace("%40", "@");
        String password = splitRequestBody[2].split(KEY_VALUE_SEPARATOR)[1];

        InMemoryUserRepository.save(new User(account, password, email));
        return "/index.html";
    }

    private String getLocationIfRedirect(String status, String path) {
        if (status.startsWith("302")) {
            return "Location: " + path + SPACE + LINE_FEED;
        }
        return "";
    }

    private String getStatus(String prevPath, String path) {
        if (!isSamePage(prevPath, path) && !prevPath.equals(path)) {
            return HttpStatus.REDIRECT.getHttpStatusCode() + SPACE + HttpStatus.REDIRECT.getHttpStatusMessage();
        }
        return HttpStatus.OK.getHttpStatusCode() + SPACE + HttpStatus.OK.getHttpStatusMessage();
    }

    private static boolean isSamePage(String prevPath, String path) {
        return (prevPath + HTML_SUFFIX).equals(path);
    }

    private String processGetRequest(String path, String jSessionId) {
        if (isRequest(path)) {
            if (path.equals("/")) {
                return path;
            }
            if (path.equals("/login")) {
                Sessions sessions = new Sessions();
                if (sessions.isAlreadyLogin(jSessionId)) {
                    return "/index.html";
                }
            }
            return path + HTML_SUFFIX;
        }
        return path;
    }

    private String getRedirectPath(String password, User user) {
        String path;
        if (user.checkPassword(password)) {
            path = "/index.html";
        } else {
            path = "/401.html";
        }
        return path;
    }

    private String getContentType(String path) {
        String contentType = "Content-Type: ";

        if (isStaticPath(path)) {
            return contentType + "text/css;charset=utf-8";
        }
        return contentType + "text/html;charset=utf-8";
    }

    private boolean isStaticPath(String path) {
        for (String staticPath : STATIC_PATH) {
            if (path.endsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }

    private String getContent(String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private boolean isRequest(String path) {
        return !isStaticPath(path) && !path.endsWith(HTML_SUFFIX);
    }

}
