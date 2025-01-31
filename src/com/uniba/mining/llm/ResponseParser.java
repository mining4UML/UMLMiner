package com.uniba.mining.llm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParser {

    public static ParsedResponse parseResponse(String rawResponse) {
        return new ParsedResponse(
            extractTagContent(rawResponse, "\\[STARTANSWER\\](.*?)\\[ENDANSWER\\]"),
            extractTagContent(rawResponse, "<<QUESTION>>(.*?)<</QUESTION>>"),
            extractTagContent(rawResponse, "<<CONTEXT>>(.*?)<</CONTEXT>>"),
            extractTagContent(rawResponse, "<<SYS>>(.*?)<</SYS>>")
        );
    }

    private static String extractTagContent(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}
