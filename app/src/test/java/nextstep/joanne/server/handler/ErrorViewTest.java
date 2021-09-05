package nextstep.joanne.server.handler;

import nextstep.joanne.server.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ErrorViewTest {

    @DisplayName("HttpStatus에 해당하는 View를 반환한다.")
    @Test
    void viewOf() {
        // given
        String view = ErrorView.viewOf(HttpStatus.INTERNAL_SERVER_ERROR);
        // when -then
        assertThat(view).isEqualTo("/500.html");
    }
}
