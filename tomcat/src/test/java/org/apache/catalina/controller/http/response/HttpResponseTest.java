package org.apache.catalina.controller.http.response;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("http응답 형식에 맞게 출력한다.")
    void write() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(outputStream);

        httpResponse.setStatus(200);
        httpResponse.setContentType("text/plain");
        httpResponse.setContentLength("hello world!".getBytes().length);
        httpResponse.setResponseBody("hello world!");

        httpResponse.write();
        httpResponse.getWriter().flush();

        StringWriter stringWriter = new StringWriter();
        PrintWriter result = new PrintWriter(stringWriter);
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain ",
                "Content-Length: 12 ",
                "",
                "hello world!");
        result.write(expected);

        String actual = outputStream.toString();
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
