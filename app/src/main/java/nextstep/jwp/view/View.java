package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpmessage.common.CommonHttpHeader.DELIMITER;


public class View {

    public static final String EMPTY_LINE = "";
    private static final Logger LOG = LoggerFactory.getLogger(View.class);
    private String viewPath;

    public View() {
    }

    public View(String viewPath) {
        this.viewPath = viewPath;
    }

    public void render(ModelAndView mv, HttpResponse response) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        String responseBody = resolveResponseBody();
        response.setContentLength(responseBody.getBytes().length);

        String output = processResponseAndBody(response, responseBody);
        outputStream.write(output.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String processResponseAndBody(HttpResponse response, String... body) {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(response.getResponseLine().toString());
        LOG.info("Response line : {}", response.getResponseLine().toString());
        stringJoiner.add(response.getHeaders().toString());
        stringJoiner.add(EMPTY_LINE);
        LOG.info("-------------------------------------------");

        if (body.length > 0) {
            stringJoiner.add(body[0]);
        }

        return stringJoiner.toString();
    }

    private String resolveResponseBody() throws IOException {
        Path path = new File(viewPath).toPath();
        return new String(Files.readAllBytes(path));
    }

    public void redirect(ModelAndView mv, HttpResponse response) throws IOException {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(response.getResponseLine().toString());
        LOG.info("Response line : {}", response.getResponseLine().toString());
        stringJoiner.add(response.getHeaders().toString());
        stringJoiner.add(EMPTY_LINE);
        response.getOutputStream().write(stringJoiner.toString().getBytes());
        response.getOutputStream().flush();
    }
}
