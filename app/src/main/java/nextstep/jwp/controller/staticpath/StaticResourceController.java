package nextstep.jwp.controller.staticpath;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.request.RequestHeader;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class StaticResourceController extends AbstractController {

    private static final String DEFAULT_PATH = "./static";

    @Override
    protected final void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException {
        RequestHeader requestHeader = httpRequestMessage.getHeader();
        byte[] bytesAsFile = readFileByUri(requestHeader.requestUri());
        manageResponse(httpResponseMessage, bytesAsFile);
    }

    private byte[] readFileByUri(String requestUri) throws IOException {
        String filePath = DEFAULT_PATH + requestUri;
        URL resource = getClass().getClassLoader().getResource(filePath);
        if (Objects.isNull(resource)) {
            throw supplyExceptionHandler(filePath).get();
        }
        Path path = new File(resource.getPath()).toPath();
        return Files.readAllBytes(path);
    }

    protected abstract Supplier<? extends RuntimeException> supplyExceptionHandler(String filePath);

    protected abstract void manageResponse(HttpResponseMessage httpResponseMessage, byte[] bytesAsFile);
}
