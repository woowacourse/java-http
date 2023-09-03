package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static String handle(HttpRequest request) throws IOException {
        if (request.getUri().equals("/")) {
            return getResponse("Hello world!", ContentType.HTML);
        }

        if (isStaticFile(request.getUri())) {
            String fileUrl = "static" + request.getUri();
            File file = new File(
                RequestHandler.class
                    .getClassLoader()
                    .getResource(fileUrl)
                    .getFile()
            );
            String responseBody = new String(Files.readAllBytes(file.toPath()));
            return getResponse(responseBody, ContentType.from(file.getName()));
        }

        if (request.getUri().equals("/login")) {
            return login(request);
        }

        throw new IllegalArgumentException();
    }

    private static String login(HttpRequest request) {
        Map<String, String> queryString = request.getQueryString();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryString.get("account"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(queryString.get("password"))) {
                log.info(user.toString());
                return getRedirectResponse(HttpStatus.FOUND, "/index.html");
            }
        }

        return getRedirectResponse(HttpStatus.FOUND, "/401.html");
    }

    private static boolean isStaticFile(String target) {
        String value = target.substring(target.lastIndexOf(".") + 1);
        return Arrays.stream(ContentType.values())
            .anyMatch(it -> it.name().equalsIgnoreCase(value));
    }

    private static String getRedirectResponse(HttpStatus httpStatus, String location) {
        return String.join("\r\n",
            "HTTP/1.1 " + httpStatus.code + " " + httpStatus.name() + " ",
            "Content-Type: " + ContentType.HTML.value + ";charset=utf-8 ",
            "Content-Length:  ",
            "Location: " + location);
    }

    private static String getResponse(String responseBody, ContentType contentType) {
        return getResponse(responseBody, contentType, HttpStatus.OK);
    }

    private static String getResponse(String responseBody, ContentType contentType, HttpStatus httpStatus) {
        return String.join("\r\n",
            "HTTP/1.1 " + httpStatus.code + " " + httpStatus.name() + " ",
            "Content-Type: " + contentType.value + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
