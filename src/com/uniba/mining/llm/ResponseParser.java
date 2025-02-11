package com.uniba.mining.llm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParser {

    public static ParsedResponse parseResponse(String rawResponse) {
        String answer = extractTagContent(rawResponse, "\\[STARTANSWER\\](.*?)\\[ENDANSWER\\]");
        String question = extractTagContent(rawResponse, "<<QUESTION>>(.*?)<</QUESTION>>");
        String context = extractTagContent(rawResponse, "<<CONTEXT>>(.*?)<</CONTEXT>>");
        String sys = extractTagContent(rawResponse, "<<SYS>>(.*?)<</SYS>>");

        // Se tutte le espressioni regolari falliscono, assegna rawResponse ad answer
        if (answer == null && question == null && context == null && sys == null) {
            answer = rawResponse;
        }

        return new ParsedResponse(answer, question, context, sys);
    }

    private static String extractTagContent(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}

