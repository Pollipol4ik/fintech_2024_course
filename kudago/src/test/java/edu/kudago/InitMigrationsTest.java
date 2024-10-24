package edu.kudago;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InitMigrationsTest extends IntegrationEnvironment {
    private final static String sql =
        "SELECT column_name FROM information_schema.columns WHERE table_name = ? ORDER BY ordinal_position;";

    private final static List<String> EVENTS_COLUMNS = List.of("id", "name", "date", "place_id");
    private final static List<String> LOCATIONS_COLUMNS = List.of("id", "name", "slug");


    @Test
    @DisplayName("Проверка создания таблицы events")
    @SneakyThrows
    public void tableIpAddressCreateTest() {
        List<String> actualColumns = getTableColumns("events");
        assertThat(actualColumns).containsExactlyElementsOf(EVENTS_COLUMNS);
    }

    @Test
    @DisplayName("Проверка создания таблицы locations")
    @SneakyThrows
    public void tableTranslationCreateTest() {
        List<String> actualColumns = getTableColumns("locations");
        assertThat(actualColumns).containsExactlyElementsOf(LOCATIONS_COLUMNS);
    }


    @SneakyThrows
    private List<String> getTableColumns(String tableName) {
        List<String> columns = new ArrayList<>();
        @Cleanup Connection connection = POSTGRES.createConnection("");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.add(resultSet.getString("column_name"));
                }
            }
        }
        return columns;
    }
}
