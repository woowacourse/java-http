package com.techcourse;

import com.techcourse.controller.mapper.RequestMapper;
import org.apache.catalina.Mapper;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();

        Mapper mapper = RequestMapper.getInstance();
        tomcat.start(mapper);
    }
}
