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
