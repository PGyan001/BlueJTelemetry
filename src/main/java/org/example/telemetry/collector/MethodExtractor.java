package org.example.telemetry.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class MethodExtractor {

    private static final Set<String> ACCESS = Set.of("public", "protected", "private");

    public List<String> extract(String className, String source) {
        List<String> result = new ArrayList<>();
        if (source == null || source.isBlank()) {
            return result;
        }
        String code = clean(source);
        int depth = 0;
        int start = 0;

        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '{' || c == ';') {
                if (depth == 1) {
                    addIfMethod(result, code.substring(start, i), className, c == '{');
                }
                if (c == '{') {
                    depth++;
                }
                start = i + 1;
            } else if (c == '}') {
                depth = Math.max(0, depth - 1);
                start = i + 1;
            }
        }
        return result;
    }

    private static String clean(String src) {
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < src.length()) {
            char c = src.charAt(i);
            char next = i + 1 < src.length() ? src.charAt(i + 1) : ' ';
            if (c == '/' && next == '/') {
                i = indexOrEnd(src, "\n", i);
                out.append(' ');
            } else if (c == '/' && next == '*') {
                i = Math.min(src.length(), indexOrEnd(src, "*/", i + 2) + 2);
                out.append(' ');
            } else if (src.startsWith("\"\"\"", i)) {
                i = Math.min(src.length(), indexOrEnd(src, "\"\"\"", i + 3) + 3);
                out.append(' ');
            } else if (c == '"' || c == '\'') {
                i = endOfLiteral(src, i);
                out.append(' ');
            } else if (c == '@') {
                i = endOfAnnotation(src, i);
                out.append(' ');
            } else {
                out.append(Character.isWhitespace(c) ? ' ' : c);
                i++;
            }
        }
        return out.toString();
    }

    private static int indexOrEnd(String src, String marker, int from) {
        int found = src.indexOf(marker, from);
        return found < 0 ? src.length() : found;
    }

    private static int endOfLiteral(String src, int i) {
        char quote = src.charAt(i);
        int j = i + 1;
        while (j < src.length()) {
            char c = src.charAt(j);
            if (c == '\\') {
                j += 2;
            } else if (c == quote) {
                return j + 1;
            } else if (c == '\n') {
                return j;
            } else {
                j++;
            }
        }
        return src.length();
    }

    private static int endOfAnnotation(String src, int at) {
        int j = at + 1;
        while (j < src.length() && (Character.isJavaIdentifierPart(src.charAt(j)) || src.charAt(j) == '.')) {
            j++;
        }
        int k = j;
        while (k < src.length() && src.charAt(k) == ' ') {
            k++;
        }
        if (k >= src.length() || src.charAt(k) != '(') {
            return j;
        }
        int open = 0;
        for (; k < src.length(); k++) {
            if (src.charAt(k) == '(') {
                open++;
            } else if (src.charAt(k) == ')' && --open == 0) {
                return k + 1;
            }
        }
        return src.length();
    }

    private static void addIfMethod(List<String> result, String text, String className, boolean hasBody) {
        int open = text.indexOf('(');
        int close = text.indexOf(')');
        if (open < 0 || close < open) {
            return;
        }
        String before = tidy(text.substring(0, open));
        if (before.contains("=")) {
            return;
        }
        int space = before.lastIndexOf(' ');
        String name = before.substring(space + 1);
        String header = space < 0 ? "" : before.substring(0, space);
        if (!isIdentifier(name)) {
            return;
        }
        boolean constructor = hasBody && name.equals(className) && !hasReturnType(header);
        if (!constructor && !hasReturnType(header)) {
            return;
        }
        String params = tidy(text.substring(open + 1, close));
        result.add((header.isEmpty() ? "" : header + " ") + name + "(" + params + ")");
    }

    private static boolean hasReturnType(String header) {
        StringTokenizer words = new StringTokenizer(header);
        while (words.hasMoreTokens()) {
            if (!ACCESS.contains(words.nextToken())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIdentifier(String s) {
        if (s.isEmpty() || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String tidy(String s) {
        StringJoiner out = new StringJoiner(" ");
        StringTokenizer words = new StringTokenizer(s);
        while (words.hasMoreTokens()) {
            out.add(words.nextToken());
        }
        return out.toString();
    }
}
