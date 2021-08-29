package nextstep.jwp.staticresource;

import nextstep.jwp.http.response.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StaticResourceFinder 테스트")
class StaticResourceFinderTest {

    @DisplayName("정적 리소스 찾기 테스트")
    @Test
    void findStaticResource() {
        //given
        final String resourcePath = "/index.html";
        final StaticResourceFinder staticResourceFinder = new StaticResourceFinder();

        //when
        final StaticResource staticResource = staticResourceFinder.findStaticResource(resourcePath);

        //then
        assertThat(staticResource.getContentType()).isEqualTo(ContentType.HTML);
        assertThat(staticResource.getContent()).isNotBlank();
    }
}