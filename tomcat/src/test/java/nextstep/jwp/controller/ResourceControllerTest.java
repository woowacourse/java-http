package nextstep.jwp.controller;

import javassist.NotFoundException;
import org.apache.http.RequestEntity;
import org.apache.http.ResponseEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceControllerTest {

    private final Controller controller = new ResourceController();

    @Test
    void 해당하는_자원을_찾으면_OK_응답을_반환한다() throws Exception {
        // given
        final RequestEntity requestEntity = new RequestEntity("/index.html", null);
        final ResponseEntity expected = new ResponseEntity().contentType("text/html");

        // when
        final ResponseEntity actual = controller.execute(requestEntity);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("contentLength", "content")
                .isEqualTo(expected);
    }

    @Test
    void 해당하는_자원을_찾지_못하면_예외가_발생한다() {
        // given
        final RequestEntity requestEntity = new RequestEntity("/notfound.html", null);

        // when, then
        assertThatThrownBy(() -> controller.execute(requestEntity))
                .isInstanceOf(NotFoundException.class);
    }
}
