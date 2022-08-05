package com.github.maxstepanovski.projecttreeplugin;

public class TestJavaClass {
    public String string = null;
    private int number;
    private double doubleNumber;

    public TestJavaClass(
            int number,
            double doubleNumber
    ) {
        this.number = number;
        this.doubleNumber = doubleNumber;
    }

    public TestJavaClass() {
        this.number = 0;
        this.doubleNumber = 0;
    }

    public String foo(String string, Integer integer) {
        return "";
    }
}
