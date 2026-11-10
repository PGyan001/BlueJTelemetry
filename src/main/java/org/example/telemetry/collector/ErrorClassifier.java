package org.example.telemetry.collector;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ErrorClassifier {
    private static final Map<String, String> RULES = new LinkedHashMap<>();
    static {
        RULES.put("cannot find symbol", "CANNOT_FIND_SYMBOL");
        RULES.put("incompatible types", "INCOMPATIBLE_TYPES");
        RULES.put("missing return", "MISSING_RETURN");
        RULES.put("might not have been initialized", "UNINITIALIZED_VARIABLE");
        RULES.put("unreported exception", "UNREPORTED_EXCEPTION");
        RULES.put("already defined", "DUPLICATE_DECLARATION");
        RULES.put("unreachable", "UNREACHABLE_CODE");
        RULES.put("expected", "SYNTAX_ERROR");
    }

    private ErrorClassifier() {}

    public static String classify(String message) {
        if (message == null) {
            return "OTHER";
        }
        String lower = message.toLowerCase();
        for (Map.Entry<String, String> rule : RULES.entrySet()) {
            if (lower.contains(rule.getKey())) {
                return rule.getValue();
            }
        }
        return "OTHER";
    }
}
