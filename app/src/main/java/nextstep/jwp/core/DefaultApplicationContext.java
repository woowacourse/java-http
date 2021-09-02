package nextstep.jwp.core;

import java.lang.annotation.Annotation;
import java.util.List;

public class DefaultApplicationContext implements ApplicationContext {

    private final BeanContainer beanContainer;

    public DefaultApplicationContext(String basePackage) {
        this.beanContainer = new BeanContainer();
        this.beanContainer.addBeans(componentScan(basePackage));
    }

    private List<BeanDefinition> componentScan(String basePackage) {
        return ComponentLoader.load(basePackage);
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return beanContainer.getBean(type);
    }

    @Override
    public List<Object> getBeansWithAnnotation(Class<? extends Annotation> annotation) {
        return beanContainer.getBeansWithAnnotation(annotation);
    }

    @Override
    public void insertBean(Object bean) {
        final Class<?> beanClass = bean.getClass();
        beanContainer.addBeans(new BeanDefinition(beanClass, bean));
    }
}
