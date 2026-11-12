package org.example.telemetry.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodExtractor {

    private static final Pattern NOISE = Pattern.compile(
            "//[^\\n]*|/\\*.*?\\*/|\"(?:\\\\.|[^\"\\\\\\n])*\"|'(?:\\\\.|[^'\\\\\\n])'", Pattern.DOTALL);
    private static final Pattern CANDIDATE = Pattern.compile(
            "(\\w+)\\s*\\(([^()]*)\\)\\s*(?:throws\\s+[\\w.,\\s]+?)?\\s*\\{");
    private static final Pattern ANNOTATION = Pattern.compile("@\\w+(?:\\([^)]*\\))?");
    private static final Pattern MODIFIERS = Pattern.compile("^(?:(?:public|protected|private)\\s+)*");
    private static final Pattern STATEMENT = Pattern.compile("\\b(?:new|return|else|throw)\\b|=|->");
    private static final Set<String> KEYWORDS =
            Set.of("if", "for", "while", "switch", "catch", "synchronized", "try");

    /** Signatures of the constructors and methods declared directly in the class body. */
    public List<String> extract(String className, String source) {
        if (source == null || source.isBlank()) {
            return List.of();
        }
        String code = NOISE.matcher(source).replaceAll(" ");

        int[] depth = new int[code.length() + 1];
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            depth[i + 1] = depth[i] + (c == '{' ? 1 : c == '}' ? -1 : 0);
        }

        List<String> result = new ArrayList<>();
        Matcher m = CANDIDATE.matcher(code);
        while (m.find()) {
            String name = m.group(1);
