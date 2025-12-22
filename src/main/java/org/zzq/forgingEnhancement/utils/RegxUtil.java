package org.zzq.forgingEnhancement.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxUtil {
    private static final Pattern unknowPattern = Pattern.compile("^.*品质: \\?\\?\\?$");
    private static final Pattern qualityPattern = Pattern.compile("^.*品质: 『([\\s\\S]{2})』$");
    private static final Pattern attributePattern = Pattern.compile("^.*: [+\\-]\\d+\\.\\d+%? 『([\\s\\S]{2})』$");
    private static List<Pattern> patterns = List.of(unknowPattern,qualityPattern,attributePattern);

    public static boolean loraDetection(String str){
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(str);
            if(matcher.matches()){
                return true;
            }
        }
        return false;
    }
}
