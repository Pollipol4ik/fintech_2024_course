package edu.java.fintechcourse2024.hw2;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.fintechcourse2024.hw2.dto.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class CityParser {

    private static final Logger log = LoggerFactory.getLogger(CityParser.class);

    public static void main(String[] args) {
        File validJson = new File("src/main/resources/city.json");
        File invalidJson = new File("src/main/resources/city-error.json");

        City city1 = fromJson(validJson);
        if (city1 != null) {
            saveAsXML(city1, new File("city1.xml"));
        }

        City city2 = fromJson(invalidJson);
        if (city2 != null) {
            saveAsXML(city2, new File("city2.xml"));
        }
    }

    public static City fromJson(File jsonFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        City city = null;
        try {
            city = objectMapper.readValue(jsonFile, City.class);
            log.info("JSON успешно прочитан и распарсен из файла: {}", jsonFile.getName());
        } catch (JsonParseException e) {
            log.error("Ошибка парсинга JSON файла {}: некорректный формат JSON. Подробности: {}", jsonFile.getName(), e.getMessage());
            log.debug("Подробности ошибки парсинга: ", e);
        } catch (JsonMappingException e) {
            log.error("Ошибка маппинга JSON на объект из файла {}: неверная структура JSON или несовпадающие ключи. Подробности: {}", jsonFile.getName(), e.getMessage());
            log.debug("Подробности ошибки маппинга: ", e);
        } catch (IOException e) {
            log.error("Ошибка при чтении JSON файла {}: {}", jsonFile.getName(), e.getMessage());
            log.debug("Подробности ошибки ввода/вывода: ", e);
        } catch (Exception e) {
            log.warn("Произошла непредвиденная ошибка при парсинге JSON из файла {}: {}", jsonFile.getName(), e.getMessage());
            log.debug("Подробности непредвиденной ошибки: ", e);
        }
        return city;
    }

    public static String toXML(City city) {
        if (city == null) {
            log.warn("Город не определен. Возвращаем пустой XML.");
            return "<City>\n" +
                    "  <slug></slug>\n" +
                    "  <coords>\n" +
                    "    <lat></lat>\n" +
                    "    <lon></lon>\n" +
                    "  </coords>\n" +
                    "</City>";
        }

        var coords = city.coords();
        return "<City>\n" +
                "  <slug>" + city.slug() + "</slug>\n" +
                "  <coords>\n" +
                "    <lat>" + (coords != null ? coords.lat() : "N/A") + "</lat>\n" +
                "    <lon>" + (coords != null ? coords.lon() : "N/A") + "</lon>\n" +
                "  </coords>\n" +
                "</City>";
    }

    public static void saveAsXML(City city, File file) {
        try {
            java.nio.file.Files.writeString(file.toPath(), toXML(city));
            log.info("XML успешно сохранен в файл {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при сохранении XML в файл {}: {}", file.getAbsolutePath(), e.getMessage());
            log.debug("Подробности ошибки сохранения XML: ", e);
        }
    }
}
