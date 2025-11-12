package org.zzq.forgingEnhancement.services;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxService {
    private Logger logger;
    private Pattern qualityPattern = Pattern.compile("^.*品质: 『([\\s\\S]{2})』$");
    private Pattern attributePattern = Pattern.compile("^.*: [+\\-]\\d+\\.\\d+%? 『([\\s\\S]{2})』$");

    public RegxService(Logger logger){
        this.logger = logger;
    }

    public boolean loraDetection(String str){
        List<Pattern> patterns = List.of(qualityPattern,attributePattern);
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(str);
            if(matcher.matches()){
                logger.info("lora匹配规则: " + pattern + " 匹配结果: " + matcher.group());
                return true;
            }
        }
        return false;
    }
}
