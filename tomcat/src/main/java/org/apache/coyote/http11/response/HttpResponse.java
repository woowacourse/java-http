package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.Headers;

public class HttpResponse {

    private final String protocolVersion = "HTTP/1.1";
    private final Headers headers = new Headers();
    private HttpStatus httpStatus = HttpStatus.OK;
    private ContentType contentType = ContentType.HTML;
    private String responseBody = "";

    public HttpResponse() {
    }

    // TODO: 리소스해석 책임 분리
    public static File resolveResource(final String name) {
        final String resourceName = "static" + name;
        final URL resource = Objects.requireNonNull(
                HttpResponse.class.getClassLoader().getResource(resourceName),
                "리소스를 찾을 수 없음: " + resourceName
        );
        return new File(resource.getFile());
    }

    public void setHeader(final String name, final String value) {
        headers.add(name, value);
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.httpStatus = Objects.requireNonNull(httpStatus, "HTTP 상태는 null일 수 없습니다.");
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = Objects.requireNonNull(contentType, "Content-Type은 null일 수 없습니다.");
    }

    public void setBody(final File resource) throws IOException {
        setBody(Files.readString(Objects.requireNonNull(resource, "본문 파일은 null일 수 없습니다.").toPath()));
    }

    public void setBody(final String responseBody) {
        this.responseBody = Objects.requireNonNull(responseBody, "응답 본문은 null일 수 없습니다.");
    }

    public boolean containsHeader(final String name) {
        return headers.contains(name);
    }

    public byte[] getResponse() {
        final List<String> responseLines = new ArrayList<>();
        responseLines.add(protocolVersion + " " + httpStatus.statusCode() + " " + httpStatus.name());
        responseLines.add("Content-Type: " + contentType.value() + ";charset=utf-8");
        responseLines.add("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length);
        headers.entries().forEach((name, value) -> responseLines.add(name + ": " + value));
        responseLines.add("");
        responseLines.add(responseBody);

        return String.join("\r\n", responseLines).getBytes(StandardCharsets.UTF_8);
    }

}
