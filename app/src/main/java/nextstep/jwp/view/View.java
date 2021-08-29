package nextstep.jwp.view;

import nextstep.jwp.handler.Model;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class View {

    private final String content;

    public View(String content) {
        this.content = content;
    }

    public static View of(String content) throws IOException {
        return new View(content);
    }

    public static View of(Path filePath) throws IOException {
        List<String> fileContents = Files.readAllLines(filePath);
        return new View(String.join("\n", fileContents + "\n"));
    }

    public static View empty() {
        return new View("");
    }

    public HttpResponse render(Model model) {
        HttpStatus httpStatus = model.getHttpStatus();

        if (httpStatus.isOK()) {
            return HttpResponse.ok(content);
        }

        if (httpStatus.isFound()) {
            return HttpResponse.redirect(model.getLocation());
        }

        if (httpStatus.isUnauthorized()) {
            return HttpResponse.unauthorized(content);
        }

        throw new IllegalArgumentException();
    }
}
