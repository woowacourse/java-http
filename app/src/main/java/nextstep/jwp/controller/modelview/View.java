package nextstep.jwp.controller.modelview;

import nextstep.jwp.httpmessage.ContentType;
import nextstep.jwp.httpmessage.httprequest.HttpRequest;
import nextstep.jwp.httpmessage.httpresponse.HttpResponse;
import nextstep.jwp.httpmessage.httpresponse.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

public class View {

    private static final Logger LOG = LoggerFactory.getLogger(View.class);
    private static final String STATIC_PATH = "static";

    private final String viewPath;

    public View(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpResponse.isSameHttpStatusCode(HttpStatusCode.FOUND)) {
            httpResponse.addHeader("Location", "http://" + httpRequest.getHeader("Host") + viewPath);
            return;
        }

        if (!httpResponse.getBody().toString().isEmpty()) {
            httpResponse.addHeader("Content-Type", ContentType.HTML.getValue());
            httpResponse.addHeader("Content-Length", httpResponse.getBody().toString().getBytes().length);
            return;
        }

        httpResponse.setBody(responseBodyByStaticURLPath(viewPath));
        httpResponse.addHeader("Content-Type", ContentType.getValueByUri(viewPath));
        httpResponse.addHeader("Content-Length", httpResponse.getBody().toString().getBytes().length);
    }

    private String responseBodyByStaticURLPath(String targetUri) {
        try {
            final URL url = getClass().getClassLoader().getResource(STATIC_PATH + targetUri);
            return Files.readString(new File(url.getFile()).toPath());
        } catch (IOException | NullPointerException exception) {
            LOG.info("리소스를 가져오려는 url이 존재하지 않습니다. 입력값: {}", STATIC_PATH + targetUri);
            throw new UnsupportedOperationException(String.format("리소스를 가져오려는 url이 존재하지 않습니다. 입력값: %s",
                    STATIC_PATH + targetUri));
        }
    }
}
