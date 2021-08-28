package nextstep.jwp.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HtmlViewResolverTest {

    @DisplayName("html 형식 파일이 들어오면 isSuitable에서 반환하는지 확인")
    @ParameterizedTest
    @CsvSource({"wedge/good/index.html, true", "foo/foofoo, false", "abc.html, true"})
    void isSuitableTest(String filePath, boolean expectedResult) {
        //given
        HtmlViewResolver htmlViewResolver = new HtmlViewResolver();
        //when
        //then
        assertThat(htmlViewResolver.isSuitable(filePath)).isEqualTo(expectedResult);
    }

    @DisplayName("반환되는 view 타입이 HtmlView인지 확인")
    @Test
    void getViewTest() throws IOException {
        //given
        HtmlViewResolver htmlViewResolver = new HtmlViewResolver();
        String filePath = "nextstep.html";
        //when
        View view = htmlViewResolver.getView(filePath, "");
        //then
        assertThat(view).isInstanceOf(HtmlView.class);
    }
}