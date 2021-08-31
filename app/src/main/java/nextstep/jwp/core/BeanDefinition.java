package nextstep.jwp.core;

import java.lang.annotation.Annotation;

public class BeanDefinition {

    private Class<?> clazz;
    private Object bean;
    private String beanName;

    public BeanDefinition(Class<?> clazz, Object bean, String beanName) {
        this.clazz = clazz;
        this.bean = bean;
        this.beanName = beanName;
    }

    public <U> boolean isTypeOf(Class<U> tClass) {
        return tClass.isAssignableFrom(clazz);
    }

    public Object getTarget() {
        return bean;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    public Class<?> getTargetClass() {
        return clazz;
    }
}
