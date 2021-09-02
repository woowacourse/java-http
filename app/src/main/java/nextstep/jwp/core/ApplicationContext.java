package nextstep.jwp.core;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ApplicationContext {

    <T> T getBean(Class<T> type);

    List<Object> getBeansWithAnnotation(Class<? extends Annotation> annotation);

    void insertBean(Object bean);
}
