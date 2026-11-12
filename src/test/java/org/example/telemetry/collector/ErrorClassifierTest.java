package org.example.telemetry.collector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorClassifierTest {

    @Test
    void classifiesKnownMessages() {
        assertEquals("CANNOT_FIND_SYMBOL", ErrorClassifier.classify("cannot find symbol"));
        assertEquals("SYNTAX_ERROR", ErrorClassifier.classify("';' expected"));
        assertEquals("INCOMPATIBLE_TYPES", ErrorClassifier.classify("Incompatible types: String cannot be converted to int"));
        assertEquals("MISSING_RETURN", ErrorClassifier.classify("missing return statement"));
    }

    @Test
    void unknownOrNullIsOther() {
        assertEquals("OTHER", ErrorClassifier.classify("something strange"));
        assertEquals("OTHER", ErrorClassifier.classify(null));
    }

    @Test
    void classifiesStaticContextErrors() {
        assertEquals("STATIC_CONTEXT",
                ErrorClassifier.classify("non-static method test() cannot be referenced from a static context"));
    }

    @Test
    void unexpectedTypeIsNotASyntaxError() {
        // "unexpected type" contains the word "expected", which used to win
        assertEquals("UNEXPECTED_TYPE",
                ErrorClassifier.classify("unexpected type\n  required: variable\n  found:    value"));
    }

    @Test
    void commonStudentErrorsGetTheirOwnCategory() {
        assertEquals("BAD_OPERANDS", ErrorClassifier.classify("bad operand types for binary operator '+'"));
        assertEquals("BAD_OPERANDS", ErrorClassifier.classify("incomparable types: int and String"));
        assertEquals("INVALID_DEREFERENCE", ErrorClassifier.classify("int cannot be dereferenced"));
        assertEquals("ABSTRACT_INSTANTIATION", ErrorClassifier.classify("Shape is abstract; cannot be instantiated"));
        assertEquals("FILE_NAME_MISMATCH",
                ErrorClassifier.classify("class Foo is public, should be declared in a file named Foo.java"));
        assertEquals("SYNTAX_ERROR", ErrorClassifier.classify("not a statement"));
        assertEquals("SYNTAX_ERROR", ErrorClassifier.classify("illegal start of expression"));
        assertEquals("SYNTAX_ERROR", ErrorClassifier.classify("'else' without 'if'"));
    }

    @Test
    void remainingCategoriesAreReachable() {
        assertEquals("UNINITIALIZED_VARIABLE", ErrorClassifier.classify("variable x might not have been initialized"));
        assertEquals("UNREPORTED_EXCEPTION", ErrorClassifier.classify("unreported exception IOException; must be caught"));
        assertEquals("DUPLICATE_DECLARATION", ErrorClassifier.classify("variable x is already defined in method main"));
        assertEquals("UNREACHABLE_CODE", ErrorClassifier.classify("unreachable statement"));
        assertEquals("WRONG_ARGUMENTS", ErrorClassifier.classify("method m cannot be applied to given types"));
        assertEquals("ACCESS_ERROR", ErrorClassifier.classify("age has private access in Human"));
        assertEquals("MISSING_OVERRIDE", ErrorClassifier.classify("method does not override or implement a method from a supertype"));
        assertEquals("SYNTAX_ERROR", ErrorClassifier.classify("reached end of file while parsing"));
    }

    @Test
    void matchingIsCaseInsensitive() {
        assertEquals("CANNOT_FIND_SYMBOL", ErrorClassifier.classify("CANNOT FIND SYMBOL"));
    }
}
