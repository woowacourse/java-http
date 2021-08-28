package nextstep;

import java.util.List;
import nextstep.jwp.core.BeanDefinition;
import nextstep.jwp.core.ComponentLoader;
import nextstep.jwp.core.handler.FrontHandler;
import nextstep.mockweb.request.MockRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NabomTest {


    @Test
    @DisplayName("hello")
    public void hello() throws Exception{
        final String content = MockRequest.get("/")
                .result()
                .asString();
        System.out.println(content);
    }
}
