package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.ControllerMapping;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.component.HttpMethod;
import org.apache.coyote.http11.component.FileExtension;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathInfoTest {

    @DisplayName("경로 정보를 이용하여 Controller Mapping을 반환한다.")
    @Test
    void getControllerMapping() {
        //given
        PathInfo pathInfo = new PathInfo("/login", FileExtension.HTML);

        //when
        ControllerMapping controllerMapping = pathInfo.getControllerMapping(HttpMethod.GET);

        //then
        assertThat(controllerMapping).isEqualTo(ControllerMapping.SEARCH_USER);
    }

    @DisplayName("경로 정보를 이용하여 응답 객체를 반환한다.")
    @Test
    void getHttpResponse() throws IOException {
        //given
        PathInfo pathInfo = new PathInfo("/index", FileExtension.HTML);
        HttpResponse<Object> response = new HttpResponse<>(new StatusLine(), null);

        //when
        HttpResponse<String> httpResponse = pathInfo.getHttpResponse(response);

        //then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] bytes = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        assertThat(httpResponse.getBody().getBytes()).isNotEmpty().isEqualTo(bytes);
    }
}
