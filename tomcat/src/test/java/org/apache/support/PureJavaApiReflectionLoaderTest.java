package org.apache.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.apache.support.testreflection.Reflection1;
import org.apache.support.testreflection.Reflection2;
import org.apache.support.testreflection.depth.Reflection3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PureJavaApiReflectionLoaderTest {

    @Test
    @DisplayName("특정 패키지의 하위 패키지의 모든 클래스파일을 읽는다.")
    void getClassesFromBasePackage() {
        final PureJavaApiReflectionLoader pureJavaApiReflectionLoader = new PureJavaApiReflectionLoader();
        final Set<Class<?>> classesFromBasePackage
                = pureJavaApiReflectionLoader.getClassesFromBasePackage("org.apache.support.testreflection");

        assertThat(classesFromBasePackage).contains(Reflection1.class, Reflection2.class, Reflection3.class);
    }
}