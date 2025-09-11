package org.apache.coyote.http.response;

import com.techcourse.util.FileUtil;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http.request.HttpRequest;

public record ResponseBody(
        String value
) {

    public static ResponseBody empty() {
        return new ResponseBody("");
    }

    public static ResponseBody helloWorld() {
        return new ResponseBody("Hello world!");
    }

    public static ResponseBody createBy(final HttpRequest httpRequest) {
        String fileName = FileUtil.createFileName(httpRequest.getFilePath());

        String responseBodyValue = FileUtil.readResource(fileName);
        return new ResponseBody(responseBodyValue);
    }

    public String toContentLengthHeaderLine() {
        int contentLength = value()
                .getBytes(StandardCharsets.UTF_8)
                .length;

        return "Content-Length: " + contentLength;
    }
}
