package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public HttpResponse handle(HttpRequest request) throws IOException {
        if (request.getUri().equals("/")) {
            return HttpResponse.ok("Hello world!", ContentType.HTML);
        }

        if (isStaticFile(request.getUri())) {
            return getFile(request);
        }

        if (request.getUri().equals("/login")) {
            return login(request);
        }

        if (request.getUri().equals("/register") && request.getMethod() == HttpMethod.POST) {
            return signUp(request);
        }

        throw new IllegalArgumentException();
    }

    private HttpResponse getFile(HttpRequest request) throws IOException {
        String fileUrl = "static" + request.getUri();
        File file = new File(
            RequestHandler.class
                .getClassLoader()
                .getResource(fileUrl)
                .getFile()
        );
        String responseBody = new String(Files.readAllBytes(file.toPath()));
        return HttpResponse.ok(responseBody, ContentType.from(file.getName()));
    }

    private boolean isStaticFile(String target) {
        String value = target.substring(target.lastIndexOf(".") + 1);
        return Arrays.stream(ContentType.values())
            .anyMatch(it -> it.name().equalsIgnoreCase(value));
    }

    private HttpResponse login(HttpRequest request) {
        Map<String, String> queryString = request.getQueryString();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(queryString.get("account"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(queryString.get("password"))) {
                log.info(user.toString());
                HttpResponse response = HttpResponse.found("/index.html");
                addCookie(request, response);
                return response;
            }
        }

        return HttpResponse.found("/401.html");
    }

    private void addCookie(HttpRequest request, HttpResponse response) {
        if (request.hasNotCookie()) {
            response.addCookie("JSESSIONID", UUID.randomUUID().toString());
        }
    }

    private HttpResponse signUp(HttpRequest request) {
        Map<String, String> body = request.getBody();
        InMemoryUserRepository.save(new User(
            body.get("account"),
            body.get("password"),
            body.get("email")
        ));
        return HttpResponse.found("/index.html");
    }
}
