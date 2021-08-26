package nextstep.jwp.http.response;

import nextstep.jwp.staticresource.StaticResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContentType 테스트")
class ContentTypeTest {

    @DisplayName("정적 리소스 가져오기 테스트")
    @Test
    void getStaticResource () throws Exception {
        //given
        final String resourcePath = "/index.html";
        final URL url = getClass().getClassLoader().getResource("static" + resourcePath);

        //when
        final StaticResource staticResource = ContentType.getStaticResource("html", Objects.requireNonNull(url));

        //then
        assertThat(staticResource.getContent()).isNotBlank();
        assertThat(staticResource.getContentType()).isEqualTo(ContentType.HTML);
    }
}