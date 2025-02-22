package com.nuoson.modulith;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class LlmCodingTest {
    @Test
    public void testCode() {
        List<List<String>> inputList = new ArrayList<>();
        inputList.add(List.of("hello", "world"));
        inputList.add(List.of("this", "is", "a", "test"));
        inputList.add(List.of("split", "these", "strings"));

        int maxLength = 50;
        int minLength = 5;

        List<String> output = processStrings(inputList, maxLength, minLength);
        for (String s : output) {
            System.out.println(s);
        }
    }

    private List<String> processStrings(List<List<String>> inputList, int maxLength, int minLength) {
        List<String> result = new ArrayList<>();
        StringBuilder currentString = new StringBuilder();

        for (List<String> stringList : inputList) {
            for (String word : stringList) {
                // Check if adding the next word exceeds the max length
                if (currentString.length() + word.length() > maxLength) {
                    if (currentString.length() > 0) {
                        result.add(currentString.toString());
                        currentString.setLength(0);
                    }
                } else {
                    if (currentString.length() > 0) {
                        currentString.append(" ");
                    }
                    currentString.append(word);
                }

                // If the current string is longer than max_length, split it
                while (currentString.length() > maxLength) {
                    int index = currentString.lastIndexOf(" ");
                    if (index == -1) {
                        result.add(currentString.toString());
                        currentString.setLength(0);
                    } else {
                        result.add(currentString.substring(0, index));
                        currentString.delete(0, index + 1);
                    }
                }
            }

            // Handle the last string in the list
            if (currentString.length() > 0) {
                if (currentString.length() < minLength) {
                    while (currentString.length() < minLength) {
                        currentString.append(" ");
                    }
                    if (currentString.length() > maxLength) {
                        int index = currentString.lastIndexOf(" ");
                        if (index == -1) {
                            result.add(currentString.toString());
                            currentString.setLength(0);
                        } else {
                            result.add(currentString.substring(0, index));
                            currentString.delete(0, index + 1);
                        }
                    }
                }
                result.add(currentString.toString());
                currentString.setLength(0);
            }
        }

        return result;
    }

}
