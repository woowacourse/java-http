package nextstep.jwp.view;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.model.httpmessage.response.ResponseLine;
import nextstep.jwp.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpmessage.common.CommonHttpHeader.DELIMITER;
import static nextstep.jwp.model.httpmessage.response.ResponseHeaderType.LOCATION;

public class View {

    public static final String RESPONSE_HEADER_FORMAT = "%s: %s ";
    private static final Logger LOG = LoggerFactory.getLogger(View.class);
    private static final String EMPTY_LINE = "";
    private static final String RESPONSE_LINE_FORMAT = "%s %s %s ";

    public void redirect(ModelAndView mv, HttpResponse response) throws IOException {
        response.addHeader(LOCATION, FileUtils.getRelativePath(mv.getViewName()));

        StringJoiner stringJoiner = mergeHeader(response, mv);
        write(response.getOutputStream(), stringJoiner.toString());
    }

    public void render(ModelAndView mv, HttpRequest request, HttpResponse response) throws IOException {
        String responseBody = (String) mv.getModel().get("body");
        response.setContentLength(responseBody.getBytes().length);
        responseBody = processResponseAndBody(response, mv, responseBody);
        write(response.getOutputStream(), responseBody);
    }

    protected String processResponseAndBody(HttpResponse response, ModelAndView mv, String... body) {
        StringJoiner stringJoiner = mergeHeader(response, mv);

        if (body.length > 0) {
            stringJoiner.add(body[0]);
        }

        return stringJoiner.toString();
    }

    private StringJoiner mergeHeader(HttpResponse response, ModelAndView mv) {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(responseLine(response, mv));
        headerLine(stringJoiner, response.getHeaders());
        stringJoiner.add(EMPTY_LINE);
        return stringJoiner;
    }

    private String responseLine(HttpResponse response, ModelAndView mv) {
        ResponseLine responseLine = response.getResponseLine();
        String firstLine = String.format(RESPONSE_LINE_FORMAT, responseLine.getProtocol(), mv.getStatus().value(), mv.getStatus().message());
        LOG.info("Response line : {}", firstLine);
        return firstLine;
    }

    private void headerLine(StringJoiner stringJoiner, Map<String, String> headers) {
        headers.forEach((key, value) -> {
            String headerLine = String.format(RESPONSE_HEADER_FORMAT, key, value);
            LOG.info("Response header : {}", headerLine);
            stringJoiner.add(headerLine);
        });
    }

    protected String resolveResponseBody(String viewPath) throws IOException {
        Path path = new File(viewPath).toPath();
        return new String(Files.readAllBytes(path));
    }

    protected void write(OutputStream outputStream, String responseBody) throws IOException {
        outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
