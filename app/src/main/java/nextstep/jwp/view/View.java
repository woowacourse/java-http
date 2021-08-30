package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.common.ContentType;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.util.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpmessage.common.CommonHttpHeader.DELIMITER;
import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_TYPE;


public class View {

    private String viewPath;

    public View(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(HttpResponse response) throws IOException {
        ContentType.of(viewPath).ifPresent(type -> response.addHeader(CONTENT_TYPE, type.value()));
        String body = FileUtils.readFileOfUrl(viewPath);
        response.getHeaders().setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        response.getOutputStream().write(body.getBytes());
        response.getOutputStream().flush();

    }

    public void render(Map<String, Object> model, HttpResponse response) throws IOException {
        String body = (String) model.get("body");
        response.addHeader(CONTENT_TYPE, HTML.value());
        response.getHeaders().setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        response.getOutputStream().write(body.getBytes());
        response.getOutputStream().flush();
    }

//    private String processResponseLineAndHeader(String... body) {
//        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
//        stringJoiner.add(responseLine.toString());
//        stringJoiner.add(headers.toString());
//        stringJoiner.add(EMPTY_LINE);
//        if (body.length > 0) {
//            stringJoiner.add(body[0]);
//        }
//        return stringJoiner.toString();
//    }
}
