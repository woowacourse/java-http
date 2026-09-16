package com.techcourse;

import org.qupring.Qupring;
import org.qupring.annotation.QupringApplication;

@QupringApplication
public class Application {

    public static void main(String[] args) {
        Qupring qupring = new Qupring();
        qupring.run(Application.class);
    }
}
