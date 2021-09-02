package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.http.entity.HttpStatus;

public class View {
    private String url;

    public View(String url) {
        this.url = url;
    }

    public void render(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        final URL resource = ViewResolver.class.getClassLoader().getResource(this.url);

        final Path path = new File(resource.getPath()).toPath();
        String html = Files.readString(path);

        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setHttpBody("text/html;charset=utf-8", html);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        View view = (View) o;
        return Objects.equals(url, view.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
