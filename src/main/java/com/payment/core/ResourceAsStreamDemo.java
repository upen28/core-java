package com.payment.core;

import java.io.InputStream;

//path that start with '/' i relative and that does no start with '/' is absolute
public class ResourceAsStreamDemo {

    public static void main(String... args) {
        InputStream stream = ResourceAsStreamDemo.class.getResourceAsStream("/generic-packager.xml");
        if (stream != null) {
            System.out.println("resource loaded successfully");
        } else {
            System.out.println("resource not loaded successfully");
        }
    }
}
