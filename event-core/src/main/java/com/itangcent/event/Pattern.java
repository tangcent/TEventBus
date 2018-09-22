package com.itangcent.event;

import java.util.function.Predicate;

public interface Pattern extends Predicate<String> {

    public static Pattern of(String pattern) {
        if (pattern.equals('*')) {
            return topic -> true;
        } else if (pattern.contains("\\")) {
            return new RegexPattern(pattern);
        } else if (pattern.startsWith("*") && pattern.lastIndexOf('*') == 0) {
            return new EndPattern(pattern.substring(1));
        } else if (pattern.endsWith("*") && pattern.indexOf('*') == pattern.length() - 1) {
            return new StartPattern(pattern.substring(0, pattern.length() - 1));
        } else if (pattern.contains("*") && pattern.indexOf('*') == pattern.lastIndexOf('*')) {
            return new ContainPattern(pattern);
        } else {
            return pattern::equals;
        }
    }

    public static Pattern of(String[] patterns) {
        if (patterns.length == 1) {
            return of(patterns[0]);
        }
        Pattern[] result = new Pattern[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            String pattern = patterns[i];
            result[i] = of(pattern);
        }

        return topic -> {
            for (Pattern pattern : result) {
                if (pattern.test(topic)) {
                    return true;
                }
            }
            return false;
        };
    }


    static class RegexPattern implements Pattern {
        java.util.regex.Pattern pattern;

        public RegexPattern(String pattern) {
            this.pattern = java.util.regex.Pattern.compile(pattern);
        }

        @Override
        public boolean test(String s) {
            return pattern.matcher(s).matches();
        }
    }

    static class StartPattern implements Pattern {
        String prefix;

        public StartPattern(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean test(String s) {
            return s != null && s.startsWith(prefix);
        }
    }

    static class EndPattern implements Pattern {
        String suffix;

        public EndPattern(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public boolean test(String s) {
            return s != null && s.endsWith(suffix);
        }
    }

    static class ContainPattern implements Pattern {
        String sub;

        public ContainPattern(String sub) {
            this.sub = sub;
        }

        @Override
        public boolean test(String s) {
            return s != null && s.contains(sub);
        }
    }
}
