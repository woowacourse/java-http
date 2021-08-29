package nextstep.jwp.core;

import java.lang.annotation.Annotation;
import java.util.List;
import nextstep.jwp.core.annotation.Controller;

public interface ApplicationContext {

    Object getBean(String key);

    <T> T getBean(String key, Class<T> type);

    <T> T getBean(Class<T> type);

    <T> List<T> getBeansByType(Class<T> type);

    List<Object> getBeansWithAnnotation(Class<? extends Annotation> annotation);
}
