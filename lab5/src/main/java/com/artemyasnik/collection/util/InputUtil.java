package com.artemyasnik.collection.util;

import com.artemyasnik.collection.classes.*;
import com.artemyasnik.io.IOWorker;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public final class InputUtil {
    public static StudyGroup get(IOWorker<String> ioWorker) throws IOException, InterruptedException {
        StudyGroup studyGroup = new StudyGroup();

        while (!input("Study group name ", studyGroup::setName, Function.identity(), ioWorker));

        Coordinates coordinates = new Coordinates();
        while (!input("coordinates x ", coordinates::setX, Double::valueOf, ioWorker));
        while (!input("coordinates y(greater than -365) ", coordinates::setY, Long::parseLong, ioWorker));
        studyGroup.setCoordinates(coordinates);

        while (!input("students count(greater than 0 or skip if null) ", studyGroup::setStudentsCount, Integer::parseInt, ioWorker));
        while (!input("transferred students(greater than 0) ", studyGroup::setTransferredStudents, Integer::parseInt, ioWorker));
        while (!input("semester %s ".formatted(
                Arrays.toString(Semester.values())), studyGroup::setSemesterEnum, Semester::valueOf, ioWorker));

        Person person = new Person();
        if (ioWorker.read("Make group admin[y/n](n for null)").equals("y")) {
            while (!input("Group admin name ", person::setName, Function.identity(), ioWorker));
            while (!input("passportID(type unique) ", person::setPassportID, Function.identity(), ioWorker));
            while (!input("hair color %s ".formatted(
                    Arrays.toString(com.artemyasnik.collection.classes.colors.hair.Color.values())), person::setHairColor, com.artemyasnik.collection.classes.colors.hair.Color::valueOf, ioWorker));
            while (!input("eyes color(skip if null) %s ".formatted(
                    Arrays.toString(com.artemyasnik.collection.classes.colors.eyes.Color.values())), person::setEyeColor, com.artemyasnik.collection.classes.colors.eyes.Color::valueOf, ioWorker));
            while (!input("nationality(skip if null) %s ".formatted(
                    Arrays.toString(Country.values())), person::setNationality, Country::valueOf, ioWorker));
            studyGroup.setGroupAdmin(person);
        }
        studyGroup.setGroupAdmin(null);

        while (!input("form of education(skip if null) %s ".formatted(
                Arrays.toString(FormOfEducation.values())), studyGroup::setFormOfEducation, FormOfEducation::valueOf, ioWorker));
        studyGroup.setGroupAdmin(person);

        return studyGroup;
    }

    private static <K> boolean input(
            final String fieldName,
            final Consumer<K> setter,
            final Function<String, K> parser,
            final IOWorker<String> io
    ) throws InterruptedException, IOException {
        try {
            String line = io.read(" - " + fieldName);
            if (line == null || line.equals("return")) throw new InterruptedException("called return");

            if (line.isBlank()) setter.accept(null);
            else setter.accept(parser.apply(line));

            return true;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception ex) {
            io.write(ex.getMessage() + System.lineSeparator());
            return false;
        }
    }
}
