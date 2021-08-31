package nextstep.jwp.core;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.core.exception.NotFoundBeanException;

public class BeanContainer {

    private Map<String, BeanDefinition> beanContainer;

    public BeanContainer() {
        this.beanContainer = new HashMap<>();
    }

    public void addBeans(BeanDefinition beanDefinition) {
        beanContainer.put(beanDefinition.getTargetClass().getName(), beanDefinition);
    }

    public void addBeans(List<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            beanContainer.put(beanDefinition.getTargetClass().getName(), beanDefinition);
        }
    }

    public List<Object> getBeansWithAnnotation(Class<? extends Annotation> annotation) {
        return beanContainer.values()
                .stream()
                .filter(beanDefinition -> beanDefinition.hasAnnotation(annotation))
                .map(BeanDefinition::getTarget)
                .collect(Collectors.toList());
    }

    public <T> List<T> getBeansByType(Class<T> type) {
        return beanContainer.values().stream()
                .filter(bean -> bean.isTypeOf(type))
                .map(bean -> (T) bean.getTarget())
                .collect(Collectors.toList());
    }

    public <T> T getBean(Class<T> type) {
        return beanContainer.values().stream()
                .filter(bean -> bean.isTypeOf(type))
                .map(bean -> (T) bean.getTarget())
                .findAny()
                .orElseThrow(NotFoundBeanException::new);
    }

    public <T> T getBean(String key, Class<T> type) {
        final BeanDefinition beanDefinition = beanContainer.get(key);
        if (beanDefinition == null) {
            throw new NotFoundBeanException();
        }
        return (T) beanDefinition.getTarget();
    }

    public Object getBean(String key) {
        final BeanDefinition beanDefinition = beanContainer.get(key);
        if (beanDefinition == null) {
            throw new NotFoundBeanException();
        }
        return beanDefinition.getTarget();
    }
}
