package org.example.telemetry.collector;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ErrorClassifier {
    private static final Map<String, String> RULES = new LinkedHashMap<>();
    static {
        RULES.put("cannot find symbol", "CANNOT_FIND_SYMBOL");
        RULES.put("incompatible types", "INCOMPATIBLE_TYPES");
        RULES.put("unexpected type", "UNEXPECTED_TYPE");
        RULES.put("missing return", "MISSING_RETURN");
        RULES.put("might not have been initialized", "UNINITIALIZED_VARIABLE");
        RULES.put("unreported exception", "UNREPORTED_EXCEPTION");
        RULES.put("already defined", "DUPLICATE_DECLARATION");
        RULES.put("unreachable", "UNREACHABLE_CODE");
        RULES.put("non-static", "STATIC_CONTEXT");
        RULES.put("cannot be applied", "WRONG_ARGUMENTS");
        RULES.put("has private access", "ACCESS_ERROR");
        RULES.put("does not override", "MISSING_OVERRIDE");
        RULES.put("bad operand types", "BAD_OPERANDS");
        RULES.put("incomparable types", "BAD_OPERANDS");
        RULES.put("cannot be dereferenced", "INVALID_DEREFERENCE");
        RULES.put("cannot be instantiated", "ABSTRACT_INSTANTIATION");
        RULES.put("should be declared in a file named", "FILE_NAME_MISMATCH");
        RULES.put("not a statement", "SYNTAX_ERROR");
        RULES.put("illegal start of", "SYNTAX_ERROR");
        RULES.put("without 'if'", "SYNTAX_ERROR");
        RULES.put("reached end of file", "SYNTAX_ERROR");
        RULES.put("expected", "SYNTAX_ERROR");
    }

    private ErrorClassifier() {}

    public static String classify(String message) {
        if (message == null) { return "OTHER";}
        String lower = message.toLowerCase();
        for (Map.Entry<String, String> rule : RULES.entrySet()) {
            if (lower.contains(rule.getKey())) {
                return rule.getValue();
            }
        }
        return "OTHER";
    }
}
