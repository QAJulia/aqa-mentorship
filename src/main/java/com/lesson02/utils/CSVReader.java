package com.lesson02.utils;


import com.lesson02.models.Person;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading test data from CSV files.
 */
public class CSVReader {

    /**
     * Reads Person objects from a CSV file.
     *
     * Expected CSV format:
     * name,age,email
     * John Doe,30,john@example.com
     * Jane Smith,25,jane@example.com
     *
     * @param filePath Path to the CSV file
     * @return List of Person objects
     * @throws IOException if file cannot be read
     */
    public static List<Person> readPersonsFromCSV(String filePath) throws IOException {
        List<Person> persons = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            br.readLine(); // Skip header line

            // Read each data line
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] parts = line.split(",");

                if (parts.length >= 3) {
                    Person person = Person.builder()
                            .name(parts[0].trim()) //trim removes spaces
                            .age(Integer.parseInt(parts[1].trim()))
                            .email(parts[2].trim())
                            .build();

                    persons.add(person);
                }
            }
        }

        return persons;
    }
}