package org.example.telemetry.collector;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodExtractorTest {

    private static final String SOURCE = """
            public class Human {
                private int age;
                public Human(int age) { this.age = age; }
                /** a doc comment with a { brace */
                public int getAge() { return age; }
                @Override
                public String toString() {
                    String s = "}{ (";
                    if (age > 1) { return s; }
                    return "x";
                }
                static void helper(int a,
                                   String b) throws Exception {
                    for (int i = 0; i < a; i++) { }
                }
            }
            """;

    @Test
    void extractsConstructorsAndMethodsOnly() {
        List<String> found = new MethodExtractor().extract("Human", SOURCE);
        assertEquals(List.of(
                "public Human(int age)",
                "public int getAge()",
                "public String toString()",
                "static void helper(int a, String b)"), found);
    }

    @Test
    void ignoresControlStatementsAndCalls() {
        List<String> found = new MethodExtractor().extract("Human", SOURCE);
        assertTrue(found.stream().noneMatch(s -> s.contains("if") || s.contains("for")));
    }

    @Test
    void nullOrBlankSourceGivesNothing() {
        assertTrue(new MethodExtractor().extract("A", null).isEmpty());
        assertTrue(new MethodExtractor().extract("A", "  ").isEmpty());
    }

    @Test
    void nestedClassMethodsAreNotIncluded() {
        String src = "public class A { void outer() { } class B { void inner() { } } }";
        assertEquals(List.of("void outer()"), new MethodExtractor().extract("A", src));
    }

    @Test
    void includesInterfaceAndAbstractMethodsWithoutBodies() {
        assertEquals(List.of("void run()", "default void hi()"),
                new MethodExtractor().extract("Shape", "interface Shape { void run(); int K = 3; default void hi() { } }"));
        assertEquals(List.of("abstract double area()"),
                new MethodExtractor().extract("S", "abstract class S { int n = compute(); abstract double area(); }"));
    }

    @Test
    void ignoresFieldInitialisersLambdasAndAnonymousClasses() {
        String src = "class A { Runnable r = () -> { run(); }; Object o = new Object() { void hidden() { } }; void real() { } }";
        assertEquals(List.of("void real()"), new MethodExtractor().extract("A", src));
    }

    @Test
    void handlesAnnotationsGenericsAndEnums() {
        assertEquals(List.of("public <T> T pick(List<T> items)"),
                new MethodExtractor().extract("G", "class G { @SuppressWarnings(\"unchecked\") public <T> T pick(List<T> items) { return null; } }"));
        assertEquals(List.of("Color(int v)"),
                new MethodExtractor().extract("Color", "enum Color { RED(1), GREEN(2); Color(int v) { } }"));
    }

    @Test
    void bracesInsideTextBlocksAndCharsAreIgnored() {
        String src = "class A { String t = \"\"\"\n } { (\n\"\"\"; char c = '{'; void a() { } }";
        assertEquals(List.of("void a()"), new MethodExtractor().extract("A", src));
    }

    @Test
    void brokenSourceNeverThrows() {
        assertTrue(new MethodExtractor().extract("A", "}}}{{{ ((( ;;; \" ' @ /* open").isEmpty());
    }
}
