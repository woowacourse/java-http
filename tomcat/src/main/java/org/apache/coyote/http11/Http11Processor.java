package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";
    private static final String SPACE = " ";
    private static final String QUERY_STRING_SEPARATOR = "\\?";
    private static final String MULTIPLE_QUERY_STRING_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String LINE_FEED = "\r\n";
    private static final String HTML_SUFFIX = ".html";
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static List<String> STATIC_PATH = List.of(".css", ".js");

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            byte[] bytes = new byte[2048];
            inputStream.read(bytes);
            final String request = new String(bytes);
            System.out.println(request);

            final String response = createResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(String request) throws IOException {
        String path = getPath(request);
        String method = getMethod(request);
        String prevPath = path;
        if (method.equals("GET")) {
            path = processGetRequest(path);
        } else if (method.equals("POST")) {
            if (path.equals("/login")) {
                path = processLogin(request);
            } else if (path.equals("/register")) {
                path = processRegister(request);
            }
        }
        String status = getStatus(prevPath, path);

        String protocol = getRequestElement(request, PROTOCOL_INDEX);
        String contentType = getContentType(path);

        String content = getContent(path);
        String contentLength = "Content-Length: " + content.getBytes().length;

        String response = protocol + SPACE + status + SPACE + LINE_FEED +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                getLocationIfRedirect(status, path) +
                LINE_FEED +
                content;
        return response;
    }

    private String processLogin(String request) {
        String path;
        String[] splitRequestBody = getRequestBody(request);
        String account = splitRequestBody[0].split(KEY_VALUE_SEPARATOR)[1];
        String password = splitRequestBody[1].split(KEY_VALUE_SEPARATOR)[1];
        try {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
            path = getRedirectPath(password, user);
            log.info(user.toString());
        } catch (UserNotFoundException e) {
            path =  "/401.html";
        }
        return path;
    }

    private static String[] getRequestBody(String request) {
        String[] splitRequest = request.split(LINE_FEED);
        String requestBody = splitRequest[splitRequest.length - 1].trim();
        String[] splitRequestBody = requestBody.split(MULTIPLE_QUERY_STRING_SEPARATOR);
        return splitRequestBody;
    }

    private static String processRegister(String request) {
        String[] splitRequestBody = getRequestBody(request);
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

    private String getMethod(String request) {
        return getRequestElement(request, 0);
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

    private String processGetRequest(String path) {
        if (isRequest(path)) {
//            if (haveQueryString(path)) {
//                path = processLogin(path);
//                return path;
//            }
            return path + HTML_SUFFIX;
        }
        return path;
    }

//    QueryString으로 login logic
//    private String processLogin(String path) {
//        String queryString = splitQueryString(path)[1];
//        String[] splitQueryString = queryString.split("&");
//        String account = splitQueryString[0].split(KEY_VALUE_SEPARATOR)[1];
//        String password = splitQueryString[1].split(KEY_VALUE_SEPARATOR)[1];
//        try {
//            User user = InMemoryUserRepository.findByAccount(account).orElseThrow(UserNotFoundException::new);
//            path = getRedirectPath(password, user);
//            log.info(user.toString());
//            return path;
//        } catch (UserNotFoundException e) {
//            return "/401.html";
//        }
//    }

    private String getRedirectPath(String password, User user) {
        String path;
        if (user.checkPassword(password)) {
            path = "/index.html";
        } else {
            path = "/401.html";
        }
        return path;
    }

    private boolean haveQueryString(String path) {
        Pattern pattern = Pattern.compile(QUERY_STRING_SEPARATOR);
        return pattern.matcher(path).find();
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

    private String getPath(String request) {
        return getRequestElement(request, PATH_INDEX);
    }

    private boolean isRequest(String path) {
        return !isStaticPath(path) && !path.endsWith(HTML_SUFFIX);
    }

    private String[] splitQueryString(String path) {
        return path.split(QUERY_STRING_SEPARATOR);
    }

    private String getRequestElement(String request, int index) {
        return request.split(SPACE + "|" + LINE_FEED)[index];
    }

}
