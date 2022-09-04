package org.apache.support;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Set;
import nextstep.testreflection.Reflection1;
import nextstep.testreflection.Reflection2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PureJavaApiReflectionLoaderTest {

    @Test
    @DisplayName("")
    void getClassesFromBasePackage() throws IOException {
        final PureJavaApiReflectionLoader pureJavaApiReflectionLoader = new PureJavaApiReflectionLoader();
        final Set<Class<?>> classesFromBasePackage
                = pureJavaApiReflectionLoader.getClassesFromBasePackage("nextstep.testreflection");

        assertThat(classesFromBasePackage).contains(Reflection1.class, Reflection2.class);
    }
}