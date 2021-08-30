package nextstep.jwp.http;

import java.util.Objects;

public class ViewResolver {

    private boolean isExisting;
    private String staticFile;
    private final HttpResponse httpResponse;

    public ViewResolver(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.isExisting = false;
        this.httpResponse = httpResponse;
        initStaticFile(httpRequest);
        initExistingStatus();
    }

    private void initStaticFile(HttpRequest httpRequest) {
        FileReaderInStaticFolder fileReaderInStaticFolder = new FileReaderInStaticFolder();
        this.staticFile = fileReaderInStaticFolder.read(httpRequest);
    }

    private void initExistingStatus() {
        if (!Objects.isNull(staticFile)) {
            isExisting = true;
        }
    }

    public boolean isExisting() {
        return isExisting;
    }

    public void resolve() {
        if (isExisting) {
            httpResponse.setStatus(HttpStatus.OK);
            httpResponse.setBody(staticFile);
            return;
        }
        httpResponse.setStatus(HttpStatus.NOT_FOUND);
        httpResponse.setBody("404.html");
    }
}
