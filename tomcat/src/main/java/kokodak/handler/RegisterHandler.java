package kokodak.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import kokodak.http.FormDataParser;
import kokodak.http.HttpMethod;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            final Map<String, String> formData = FormDataParser.parse(httpRequest.getBody());
            final User user = new User(formData.get("account"), formData.get("password"), formData.get("email"));
            InMemoryUserRepository.save(user);
            return HttpResponse.builder()
                               .redirect("http://localhost:8080/index.html")
                               .build();
        } else {
            final String fileName = "static/register.html";
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
}
