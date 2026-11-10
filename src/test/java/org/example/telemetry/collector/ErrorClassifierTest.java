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
}