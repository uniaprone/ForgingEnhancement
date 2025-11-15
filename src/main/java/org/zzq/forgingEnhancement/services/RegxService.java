package org.zzq.forgingEnhancement.services;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxService {
    private Logger logger;
    private static final Pattern unknowPattern = Pattern.compile("^.*品质: \\?\\?\\?$");
    private static final Pattern qualityPattern = Pattern.compile("^.*品质: 『([\\s\\S]{2})』$");
    private static final Pattern attributePattern = Pattern.compile("^.*: [+\\-]\\d+\\.\\d+%? 『([\\s\\S]{2})』$");
    private List<Pattern> patterns = List.of(unknowPattern,qualityPattern,attributePattern);
    public RegxService(Logger logger){
        this.logger = logger;
    }

    public boolean loraDetection(String str){
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(str);
            if(matcher.matches()){
//                logger.info("匹配字符串: " + str);
//                logger.info("lora匹配规则: " + pattern + " 匹配结果: " + matcher.group());
                return true;
            }
        }
        return false;
    }
}
