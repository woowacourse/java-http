package org.apache.catalina.servlet;

import static org.apache.catalina.servlet.ServletMapping.getSupportedServlet;
import static org.apache.coyote.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.catalina.exception.NotSupportedRequestException;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.Url;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ServletMappingTest {

    @Test
    void Request에_따라서_지원하는_Servlet을_반환한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/"))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // expected
        assertThat(getSupportedServlet(request)).isInstanceOf(Controller.class);
    }

    @Test
    void 지원하지_않는_Request_인_경우_NotSupportedRequestException을_반환한다() {
        // given
        HttpRequest request = new HttpRequest.Builder()
                .httpMethod(GET)
                .url(Url.from("/" + Integer.MAX_VALUE))
                .headers(HttpHeaders.getEmptyHeaders())
                .build();

        // expected
        assertThatThrownBy(() -> getSupportedServlet(request))
                .isInstanceOf(NotSupportedRequestException.class);
    }
}
