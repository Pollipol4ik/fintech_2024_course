package edu.java.fintechcourse2024.hw2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CityParser {

    private static final Logger logger = LoggerFactory.getLogger(CityParser.class);

    public static void main(String[] args) {
        File correctJsonFile = new File("city.json");
        File errorJsonFile = new File("city-error.json");

        // Обработка правильного JSON файла
        City city = City.fromJson(correctJsonFile);
        if (city != null) {
            logger.debug("Содержимое объекта City: {}", city);
            city.saveAsXML(new File("city.xml"));
        } else {
            logger.warn("Не удалось распарсить корректный JSON файл.");
        }

        // Обработка JSON файла с ошибками
        City erroneousCity = City.fromJson(errorJsonFile);
        if (erroneousCity != null) {
            logger.debug("Содержимое объекта City из файла с ошибками: {}", erroneousCity);
            erroneousCity.saveAsXML(new File("city-error.xml"));
        } else {
            logger.warn("Не удалось распарсить JSON файл с ошибками.");
        }
    }
}
