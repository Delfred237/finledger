package com.portfolio.finledger.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Wraps Scanner to provide safe, validated console input.
 *
 * This class never throws exceptions to the caller.
 * Invalid input returns Optional.empty() or a default value.
 */
public class ConsoleReader {

    private final Scanner scanner;

    public ConsoleReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads a non-empty string.
     */
    public Optional<String> readNonEmptyString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(input);
    }

    /**
     * Reads a string that can be empty.
     */
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Reads an integer choice.
     */
    public Optional<Integer> readInt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    /**
     * Reads a BigDecimal amount.
     */
    public Optional<BigDecimal> readAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        try {
            return Optional.of(new BigDecimal(input));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    /**
     * Reads a date in yyyy-MM-dd format.
     */
    public Optional<LocalDate> readDate(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocalDate.parse(input));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /**
     * Reads a yes/no confirmation.
     */
    public boolean readConfirmation(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
}