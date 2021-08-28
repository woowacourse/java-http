package nextstep.jwp.http;

import java.util.Objects;

public class ViewResolver {
    private boolean isExisting;
    private String staticFile;

    public ViewResolver(HttpRequest httpRequest) {
        this.isExisting = false;
        initStaticFile(httpRequest);
        initExistingStatus();
    }

    private void initStaticFile(HttpRequest httpRequest) {
        StaticFileReader staticFileReader = new StaticFileReader();
        this.staticFile = staticFileReader.read(httpRequest);
    }

    private void initExistingStatus() {
        if (!Objects.isNull(staticFile)) {
            isExisting = true;
        }
    }

    public boolean isExisting() {
        return isExisting;
    }

    public HttpResponse resolve() {
        if (isExisting) {
            return new HttpResponse(HttpStatus.OK, staticFile);
        }
        return new HttpResponse(HttpStatus.NOT_FOUND, "404.html");
    }
}
