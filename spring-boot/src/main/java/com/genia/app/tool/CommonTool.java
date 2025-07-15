package com.genia.app.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonTool {

    @Tool(description = "Reverse a given string. Takes one argument: 'text' (the string to reverse).")
    public String reverseString(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    @Tool(description = "Get the current time and location. Takes no arguments.")
    public String getCurrentTimeAndLocation() {

        log.debug("MCP Tool 'getCurrentTimeAndLocation' invoked. Current time is " + new java.util.Date());
        String currentTime = "Sunday, July 13, 2025 at 9:57:27 PM CST";
        String currentLocation = "San Pablo, Heredia Province, Costa Rica";
        String response = String.format("The current time is %s and the current location is %s.",
                currentTime, currentLocation);
        return response;
    }

    @Tool(description = "Calculate the square of an integer. Takes one argument: 'number' (the integer to square).")
    public String squareNumber(int number) {

        log.debug("MCP Tool 'squareNumber' invoked with number: " + number);
        return String.valueOf(number * number);

    }
}