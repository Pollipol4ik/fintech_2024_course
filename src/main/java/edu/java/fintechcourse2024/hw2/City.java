package edu.java.fintechcourse2024.hw2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class City {

    private static final Logger logger = LoggerFactory.getLogger(City.class);

    private String slug;
    private Coordinates coords;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coordinates {
        private double lat;
        private double lon;
    }

    public static City fromJson(File jsonFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        City city = null;
        try {
            city = objectMapper.readValue(jsonFile, City.class);
            logger.info("JSON успешно прочитан и распарсен.");
        } catch (IOException e) {
            logger.error("Ошибка при чтении JSON файла: {}", e.getMessage());
        }
        return city;
    }

    public String toXML() {
        return "<City>\n" +
                "  <slug>" + slug + "</slug>\n" +
                "  <coords>\n" +
                "    <lat>" + coords.getLat() + "</lat>\n" +
                "    <lon>" + coords.getLon() + "</lon>\n" +
                "  </coords>\n" +
                "</City>";
    }

    public void saveAsXML(File file) {
        try {
            java.nio.file.Files.writeString(file.toPath(), toXML());
            logger.info("XML успешно сохранен в файл {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Ошибка при сохранении XML в файл: {}", e.getMessage());
        }
    }
}
