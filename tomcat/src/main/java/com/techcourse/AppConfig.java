package com.techcourse;

import org.apache.catalina.session.SessionGenerator;

public interface AppConfig {

    SessionGenerator sessionGenerator();
}
