package nextstep.jwp.dispatcher.adapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.dispatcher.mapping.FileAccessHandlerMapping;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.ContentType;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpStatus;

public class FileAccessHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(HandlerMapping handlerMapping) {
        return handlerMapping instanceof FileAccessHandlerMapping;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, Handler handler) {
        Path path = getFilePath(httpRequest.getRequestURI());

        try (InputStream fileInputStream = new FileInputStream(path.toFile())) {
            byte[] bytes = fileInputStream.readAllBytes();
            String content = new String(bytes);

            httpResponse.setContent(content);
        } catch (IOException e) {
            throw new RuntimeException("존재하지 않는 파일입니다. - 404");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.valueByFileExtension(parseFileExtension(
            httpRequest.getRequestURI())).getDescription());
        headers.addHeader("Content-Length", String.valueOf(httpResponse.getContentAsString().length()));

        httpResponse.setHeaders(headers);
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setVersionOfProtocol("HTTP/1.1");
    }

    private String parseFileExtension(String fileName) {
        String[] splittedFileName = fileName.split("\\.");
        return splittedFileName[1];
    }

    private Path getFilePath(String fileName) {
        String file = "./static" + fileName;
        URL fileResource = getClass().getClassLoader().getResource(file);
        if (Objects.isNull(fileResource)) {
            throw new RuntimeException("존재하지 않는 파일 입니다. - 404");
        }
        return Paths.get(fileResource.getPath());
    }
}
