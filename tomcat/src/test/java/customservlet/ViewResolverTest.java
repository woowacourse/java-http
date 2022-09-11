package customservlet;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.ResourceUri;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    private HttpResponse httpResponse;
    private ViewResolver viewResolver;

    @BeforeEach
    void setUp() {
        this.httpResponse = mock(HttpResponse.class);
        this.viewResolver = new ViewResolver();
    }

    @Test
    void ViewName에_dot가_포함되있으면_그대로_응답값에_추가한다() {
        // given, when
        viewResolver.resolve("/index.html", httpResponse);

        // then
        verify(httpResponse).setResourceUri(refEq(ResourceUri.from("/index.html")));
    }

    @Test
    void ViewName에_dot가_포함되있지_않는다면_파싱후에_응답값에_추가한다() {
        // given, when
        viewResolver.resolve("index", httpResponse);

        // then
        verify(httpResponse).setResourceUri(refEq(ResourceUri.from("/index.html")));
    }
}
