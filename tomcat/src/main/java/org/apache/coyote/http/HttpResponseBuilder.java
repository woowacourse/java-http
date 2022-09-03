package org.apache.coyote.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpResponseBuilder {

    private HttpStatus httpStatus;
    private Map<String, String> header;
    private String body;

    public HttpResponseBuilder(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.header = new HashMap<>();
        this.body = "";
    }

    public HttpResponseBuilder header(Header inputHeader){
        for (Entry<String, String> value : inputHeader.values().entrySet()) {
            header.put(value.getKey(), value.getValue());
        }
        return this;
    }

    public HttpResponseBuilder body(Path path){
        try {
            header.put("Content-Type", Files.probeContentType(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
        }

        this.body = readFile(path);
        return this;
    }

    public HttpResponseBuilder body(String bodyValue){
        header.put("Content-Type", "text/html");
        this.body = bodyValue;
        return this;
    }

    public HttpResponse build(){
        if(httpStatus == HttpStatus.NOT_FOUND){
            body(Paths.get(Objects.requireNonNull(
                            getClass()
                            .getClassLoader()
                            .getResource("static/404.html"))
                            .getPath()));
        }
        return new HttpResponse(httpStatus, header, body);
    }

    private String readFile(final Path filePath) {
        try {
            return new String(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

}
