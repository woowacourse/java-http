package nextstep.jwp.http.response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import nextstep.jwp.http.common.ContentType;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;

public class HttpResponse {

    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.httpStatus = HttpStatus.OK;
        this.httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        this.responseBody = new ResponseBody("");
    }

    public void addHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addResponseBody(final String text) {
        byte[] responseBody = text.getBytes(StandardCharsets.UTF_8);

        httpHeaders.add("Content-Type", ContentType.TEXT_HTML.getType());
        httpHeaders.add("Content-Length", String.valueOf(responseBody.length));

        this.responseBody = new ResponseBody(text);
    }

    public void addResponseBody(final File file) throws IOException {
        Path path = file.toPath();
        byte[] responseBody = Files.readAllBytes(file.toPath());

        httpHeaders.add("Content-Type", Files.probeContentType(path));
        httpHeaders.add("Content-Length", String.valueOf(responseBody.length));

        this.responseBody = new ResponseBody(new String(responseBody));
    }

    @Override
    public String toString() {
        String response = String.join("\r\n",
            "HTTP/1.1" + " " + httpStatus.getCode() + " " + httpStatus.getDescription() + " ",
            "Content-Type: " + httpHeaders.getHeader("Content-Type") + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getValue().getBytes().length + " ",
            "",
            responseBody.getValue());

        return response;
    }
}
