package ss.shell.tests;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @org.junit.jupiter.api.Test
    void sum() {
        System.out.println("sum");
        int a = 2;
        int b = 3;
        Operator instance = new Operator();
        int result = instance.sum(a, b);
        assertEquals(result, 5);
    }

    @org.junit.jupiter.api.Test
    void multiplication() {
        System.out.println("multiplication");
        int a = 5;
        int b = 3;
        Operator instance = new Operator();
        int result = instance.multiplication(a, b);
        assertEquals(result, 15);
    }

    @org.junit.jupiter.api.Test
    void helloWorld() {
        System.out.println("helloWorld");
        Operator instance = new Operator();
        String result = instance.helloWorld();
        assertEquals(result, "Hello World");
    }
}