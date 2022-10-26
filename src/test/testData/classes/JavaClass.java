package com.github.maxstepanovski.projecttreeplugin;

public class JavaClass {
    public String string = null;
    private int number;
    private double doubleNumber;

    public JavaClass(
            int number,
            double doubleNumber
    ) {
        this.number = number;
        this.doubleNumber = doubleNumber;
    }

    public JavaClass() {
        this.number = 0;
        this.doubleNumber = 0;
    }

    public String foo(String string, Integer integer) {
        return "";
    }
}