package com.week03.utils;

import com.week03.models.Person;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading test data from YAML files.
 */
public class YAMLReader {

    /**
     * Reads Person objects from a YAML file.
     *
     * Expected YAML format:
     * people:
     *   - name: John Doe
     *     age: 30
     *     email: john@example.com
     *   - name: Jane Smith
     *     age: 25
     *     email: jane@example.com
     *
     * @param filePath Path to the YAML file
     * @return List of Person objects
     * @throws IOException if file cannot be read
     */
    public static List<Person> readPersonsFromYAML(String filePath) throws IOException {
        List<Person> persons = new ArrayList<>();

        Yaml yaml = new Yaml();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Map<String, List<Map<String, Object>>> data = yaml.load(fis);

            if (data == null || !data.containsKey("people")) {
                return persons; // Return empty list if no people data
            }

            List<Map<String, Object>> peopleData = data.get("people");

            for (Map<String, Object> personData : peopleData) {
                String name = (String) personData.get("name");
                Integer age = (Integer) personData.get("age");
                String email = (String) personData.get("email");

                Person person = Person.builder()
                        .name(name)
                        .age(age)
                        .email(email)
                        .build();

                persons.add(person);
            }
        }

        return persons;
    }
}
