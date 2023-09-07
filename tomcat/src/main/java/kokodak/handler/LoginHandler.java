package kokodak.handler;

import static kokodak.http.HttpStatusCode.FOUND;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kokodak.http.FormDataParser;
import kokodak.http.HttpMethod;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import kokodak.http.Session;
import kokodak.http.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private List<Argument> arguments = new ArrayList<>();

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            final Map<String, String> formData = FormDataParser.parse(httpRequest.getBody());
            final User user = InMemoryUserRepository.findByAccount(formData.get("account"))
                                                    .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
            if (user.checkPassword(formData.get("password"))) {
                log.info("user = {}", user);
                final String sessionId = UUID.randomUUID().toString();
                final Session session = new Session(sessionId);
                session.setAttribute("user", user);
                SessionManager.addSession(session);
                return HttpResponse.builder()
                                   .redirect("http://localhost:8080/index.html")
                                   .cookie("JSESSIONID=" + sessionId)
                                   .build();
            } else {
                return HttpResponse.builder()
                                   .redirect("http://localhost:8080/401.html")
                                   .build();
            }
        } else {
            final Session argument = arguments.stream()
                                             .filter(ag -> ag.getImlClass() == Session.class)
                                             .map(ag -> (Session) ag)
                                             .findFirst()
                                             .orElseThrow(IllegalArgumentException::new);
            if (argument.getId() != null) {
                return HttpResponse.builder()
                                   .redirect("http://localhost:8080/index.html")
                                   .build();
            }
            final String fileName = "static/login.html";
            final URL resourceUrl = getClass().getClassLoader().getResource(fileName);
            final Path path = new File(resourceUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));
            return HttpResponse.builder()
                               .header("Content-Type", "text/html;charset=utf-8")
                               .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                               .body(responseBody)
                               .build();
        }
    }

    @Override
    public List<Class<? extends Argument>> requiredArguments() {
        return List.of(Session.class);
    }

    @Override
    public void setArguments(final List<Argument> arguments) {
        this.arguments = arguments;
    }
}
