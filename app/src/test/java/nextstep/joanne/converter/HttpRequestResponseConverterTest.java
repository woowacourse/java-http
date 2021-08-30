package nextstep.joanne.converter;

import nextstep.joanne.MockSocket;
import nextstep.joanne.http.HttpMethod;
import nextstep.joanne.http.HttpStatus;
import nextstep.joanne.http.request.HttpRequest;
import nextstep.joanne.http.response.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestResponseConverterTest {

    @Test
    void convertToHttpRequest() throws IOException {
        String request = makeGetRequest("/index");
        MockSocket connection = new MockSocket(request);
        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = HttpRequestResponseConverter.convertToHttpRequest(bufferedReader);
        assertAll(
                () -> assertThat(httpRequest.uriEquals("/index.html")).isTrue(),
                () -> assertThat(httpRequest.isEqualsMethod(HttpMethod.GET)).isTrue()
        );

        connection.close();
        inputStream.close();
        bufferedReader.close();
    }

    private String makeGetRequest(String uri) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    @Test
    void convertToHttpResponse() {
        String uri = "/index.html";
        HttpResponse response = HttpRequestResponseConverter.convertToHttpResponse(HttpStatus.OK, uri, "text/html");

        URL resource = getClass().getClassLoader().getResource("static" + uri);
        assert resource != null;
        final Path filePath = new File(resource.getPath()).toPath();

        assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.body()).isEqualTo(make200OkBody(Files.readString(filePath)))
        );
    }

    private String make200OkBody(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}