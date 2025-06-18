package com.artemyasnik.collection.util;

import com.artemyasnik.collection.classes.*;
import com.artemyasnik.collection.id.IdGenerator;
import com.artemyasnik.io.IOWorker;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public final class InputUtil {
    private static final int MIN_COORDINATE_Y = -365;
    private static final int MIN_STUDENTS_COUNT = 0;
    private static final int MIN_TRANSFERRED_STUDENTS = 0;

    public static StudyGroup get(IOWorker<String> ioWorker) throws IOException, InterruptedException {
        StudyGroup studyGroup = new StudyGroup();

        // Ввод обязательных полей
        studyGroup.setName(getInput("Study group name: ",
                s -> !s.isBlank(),
                Function.identity(),
                "Name cannot be null or empty",
                ioWorker));

        Coordinates coordinates = new Coordinates();
        coordinates.setX(getInput("Coordinates x: ",
                x -> true,
                Double::parseDouble,
                "Invalid coordinate x value",
                ioWorker));

        coordinates.setY(getInput("Coordinates y (greater than " + MIN_COORDINATE_Y + "): ",
                y -> {
                    try {
                        return Long.parseLong(y) > MIN_COORDINATE_Y;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                Long::parseLong,
                "Coordinate y must be a number greater than " + MIN_COORDINATE_Y,
                ioWorker));
        studyGroup.setCoordinates(coordinates);

        // Ввод необязательных полей
        studyGroup.setStudentsCount(getOptionalInput(
                "Students count (greater than " + MIN_STUDENTS_COUNT + " or skip): ",
                count -> {
                    try {
                        return Integer.parseInt(count) > MIN_STUDENTS_COUNT;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                Integer::parseInt,
                "Student count must be a number greater than " + MIN_STUDENTS_COUNT,
                ioWorker));

        studyGroup.setTransferredStudents(getInput(
                "Transferred students (greater than " + MIN_TRANSFERRED_STUDENTS + "): ",
                count -> {
                    try {
                        return Integer.parseInt(count) > MIN_TRANSFERRED_STUDENTS;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                Integer::parseInt,
                "Transferred students must be a number greater than " + MIN_TRANSFERRED_STUDENTS,
                ioWorker));

        studyGroup.setSemesterEnum(getInput(
                "Semester " + Arrays.toString(Semester.values()) + ": ",
                input -> Arrays.stream(Semester.values())
                        .anyMatch(e -> e.name().equalsIgnoreCase(input)),
                Semester::valueOf,
                "Invalid semester value",
                ioWorker));

        // Ввод администратора группы
        if (confirm("Make group admin? [y/n]: ", ioWorker)) {
            studyGroup.setGroupAdmin(getPersonFromInput(ioWorker));
        }

        // Ввод формы обучения
        studyGroup.setFormOfEducation(getOptionalInput(
                "Form of education " + Arrays.toString(FormOfEducation.values()) + " (skip if null): ",
                input -> Arrays.stream(FormOfEducation.values())
                        .anyMatch(e -> e.name().equalsIgnoreCase(input)),
                FormOfEducation::valueOf,
                "Invalid form of education",
                ioWorker));

        // Установка ID
        studyGroup.setId(IdGenerator.getInstance().generateId());

        return studyGroup;
    }

    private static Person getPersonFromInput(IOWorker<String> ioWorker)
            throws IOException, InterruptedException {
        Person person = new Person();

        person.setName(getInput("Group admin name: ",
                s -> !s.isBlank(),
                Function.identity(),
                "Name cannot be empty",
                ioWorker));

        person.setPassportID(getInput("Passport ID (unique): ",
                s -> !s.isBlank(),
                Function.identity(),
                "Passport ID cannot be empty",
                ioWorker));

        person.setHairColor(getInput(
                "Hair color " + Arrays.toString(com.artemyasnik.collection.classes.colors.hair.Color.values()) + ": ",
                input -> Arrays.stream(com.artemyasnik.collection.classes.colors.hair.Color.values())
                        .anyMatch(e -> e.name().equalsIgnoreCase(input)),
                com.artemyasnik.collection.classes.colors.hair.Color::valueOf,
                "Invalid hair color",
                ioWorker));

        person.setEyeColor(getOptionalInput(
                "Eye color " + Arrays.toString(com.artemyasnik.collection.classes.colors.eyes.Color.values()) + " (skip if null): ",
                input -> Arrays.stream(com.artemyasnik.collection.classes.colors.eyes.Color.values())
                        .anyMatch(e -> e.name().equalsIgnoreCase(input)),
                com.artemyasnik.collection.classes.colors.eyes.Color::valueOf,
                "Invalid eye color",
                ioWorker));

        person.setNationality(getOptionalInput(
                "Nationality " + Arrays.toString(Country.values()) + " (skip if null): ",
                input -> Arrays.stream(Country.values())
                        .anyMatch(e -> e.name().equalsIgnoreCase(input)),
                Country::valueOf,
                "Invalid nationality",
                ioWorker));

        return person;
    }

    private static <T> T getInput(String prompt,
                                  Function<String, Boolean> validator,
                                  Function<String, T> parser,
                                  String errorMessage,
                                  IOWorker<String> ioWorker)
            throws IOException, InterruptedException {
        while (true) {
            try {
                String input = ioWorker.read(prompt).trim();
                if (input.equalsIgnoreCase("exit")) {
                    throw new InterruptedException("Operation cancelled by user");
                }
                if (validator.apply(input)) {
                    return parser.apply(input);
                }
                ioWorker.write(errorMessage + "\n");
            } catch (Exception e) {
                ioWorker.write("Error: " + e.getMessage() + "\n");
            }
        }
    }

    private static <T> T getOptionalInput(String prompt,
                                          Function<String, Boolean> validator,
                                          Function<String, T> parser,
                                          String errorMessage,
                                          IOWorker<String> ioWorker)
            throws IOException, InterruptedException {
        while (true) {
            try {
                String input = ioWorker.read(prompt).trim();
                if (input.equalsIgnoreCase("exit")) {
                    throw new InterruptedException("Operation cancelled by user");
                }
                if (input.isEmpty()) {
                    return null;
                }
                if (validator.apply(input)) {
                    return parser.apply(input);
                }
                ioWorker.write(errorMessage + "\n");
            } catch (Exception e) {
                ioWorker.write("Error: " + e.getMessage() + "\n");
            }
        }
    }

    private static boolean confirm(String prompt, IOWorker<String> ioWorker)
            throws IOException, InterruptedException {
        while (true) {
            String input = ioWorker.read(prompt).trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            ioWorker.write("Please answer 'y' or 'n'\n");
        }
    }
}