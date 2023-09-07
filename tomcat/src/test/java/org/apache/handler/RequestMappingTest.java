package org.apache.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.request.HttpRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMappingTest {

    @ParameterizedTest
    @ValueSource(strings = {"/", "/login", "/register"})
    void HTTP_요청에_해당하는_핸들러를_반환한다(String url) throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "GET " + url + " HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        RequestMapping requestMapping = new RequestMapping();
        AbstractController controller = requestMapping.findController(httpRequest);

        assertThat(controller).isNotNull();
    }

    @Test
    void HTTP_요청에_해당하는_핸들러가_없으면_파일_핸들러를_반환한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "GET /notFound HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        RequestMapping requestMapping = new RequestMapping();
        AbstractController controller = requestMapping.findController(httpRequest);

        assertThat(controller).isInstanceOf(FileController.class);
    }
}
