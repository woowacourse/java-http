package nextstep.jwp;

import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceTest {

    @Test
    void 자원을_찾으면_해당_값을_읽어_문자열로_반환한다() throws NotFoundException {
        // given
        final Resource resource = new Resource("/index.html");

        // when, then
        assertThat(resource.read()).isNotNull();
    }

    @Test
    void 자원을_찾지_못하면_예외를_발생한다() {
        // given, when. then
        final Resource resource = new Resource("/notfound.html");
        assertThatThrownBy(resource::read)
                .isInstanceOf(NotFoundException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"/index.html,text/html", "/css/styles.css,text/css"})
    void 자원의_contentType을_불러온다(final String path, final String contentType) throws NotFoundException {
        // given
        final Resource resource = new Resource(path);
        // when, then
        assertThat(resource.getContentType()).isEqualTo(contentType);
    }
}
