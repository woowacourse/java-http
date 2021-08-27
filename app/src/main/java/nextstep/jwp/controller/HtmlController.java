package nextstep.jwp.controller;

import nextstep.jwp.exception.HttpUriNotFoundException;
import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.request.RequestHeader;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class HtmlController extends AbstractController {

    private static final String DEFAULT_PATH = "./static";
    private static final HtmlController instance = new HtmlController();

    private HtmlController() {
    }

    public static HtmlController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) throws IOException {
        RequestHeader requestHeader = httpRequestMessage.getHeader();
        String filePath = DEFAULT_PATH + requestHeader.requestUri();
        URL resource = getClass().getClassLoader().getResource(filePath);
        if (Objects.isNull(resource)) {
            throw new HttpUriNotFoundException(String.format("해당 파일을 찾을 수 없습니다.(%s)", filePath));
        }
        Path path = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(path);

        MessageBody messageBody = new MessageBody(bytes);
        httpResponseMessage.setMessageBody(messageBody);

        httpResponseMessage.setStatusCode(HttpStatusCode.OK);
        httpResponseMessage.putHeader("Content-Type", "text/html;charset=utf-8");
        httpResponseMessage.putHeader("Content-Length", messageBody.contentLength());
    }
}
