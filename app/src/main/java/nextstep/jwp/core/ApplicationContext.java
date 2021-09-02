package nextstep.jwp.core;

import java.lang.annotation.Annotation;
import java.util.List;
import nextstep.jwp.mvc.mapping.HandlerMapping;

public interface ApplicationContext {

    <T> T getBean(Class<T> type);

    List<Object> getBeansWithAnnotation(Class<? extends Annotation> annotation);

    void insertBean(Object bean);
}
