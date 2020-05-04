package com.travel.travelapi.config;


import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import java.lang.module.Configuration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MybatisExtendedLanguageDriver extends XMLLanguageDriver
        implements LanguageDriver {
    private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        Matcher matcher = inPattern.matcher(script);
        if (matcher.find()) {
            script = matcher.replaceAll("(<foreach collection=\"$1\" item=\"__item\" separator=\",\" >#{__item}</foreach>)");
        }
        script = "<script>" + script + "</script>";
        var sqlSource = super.createSqlSource(configuration, script, parameterType);
        return sqlSource;
    }
}