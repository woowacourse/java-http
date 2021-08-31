package nextstep.mvc;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.mvc.handler.ParameterClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DispatcherServletTest {

    @Test
    @DisplayName("test")
    public void test() throws Exception{
//        ApplicationContext ac = new DefaultApplicationContext("nextstep");
//        final DispatcherServlet dispatcherServlet = new DispatcherServlet(ac);
//        final HandlerMapping bean = ac.getBean(HandlerMapping.class);
//        System.out.println(bean);

        Map<String,String> map = new HashMap<>();
        map.put("hello", "nabom");
        map.put("bye", "bomin");

        final ParameterClass parameterClass = new ParameterClass(String.class);
//        final NabomTest nabomTest = (NabomTest) parameterClass.createInstance(map);
//
//        System.out.println(nabomTest.getHello());
//        System.out.println(nabomTest.getBye());
    }
}
