package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.request.QueryParameters;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.StartLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void start_line을_파싱한다() {
        // given
        String fileUri = "GET /nextstep.txt HTTP/1.1";

        // when
        Request request = Request.of(new StartLine(fileUri));

        // then
        Assertions.assertAll(

                () -> assertThat(request.getPath().getFileName()).isEqualTo("nextstep.txt"),
                () -> assertThat(request.getQueryParameters().isEmpty()).isTrue()
        );
    }

    @Test
    void query_parameter를_가진다() {
        // given
        String fileUri = "GET /login?account=gugu&password=gugugugu HTTP/1.1";
        Request request = Request.of(new StartLine(fileUri));

        // when
        QueryParameters queryParameters = request.getQueryParameters();

        // then
        Assertions.assertAll(

                () -> assertThat(queryParameters.getAccount()).isEqualTo("gugu"),
                () -> assertThat(queryParameters.getPassword()).isEqualTo("gugugugu")
        );
    }
}
