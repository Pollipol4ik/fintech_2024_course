package edu.kudago.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import edu.kudago.dto.Category;
import edu.kudago.dto.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Testcontainers
public class ApiClientTest {

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0");

    @Autowired
    private ApiClient apiClient;

    @Value("${kudago.categories-endpoint}")
    private String categoriesEndpoint;

    @Value("${kudago.locations-endpoint}")
    private String locationsEndpoint;

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("api.base-url", wiremockServer::getBaseUrl);
    }

    @Test
    void testFetchCategoriesSuccess() {
        WireMock.configureFor(wiremockServer.getHost(), wiremockServer.getFirstMappedPort());
        String categoriesJson = "[{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"},{\"id\":2,\"slug\":\"music\",\"name\":\"Music\"}]";
        stubFor(get(urlEqualTo(categoriesEndpoint))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(categoriesJson)));
        Category[] categories = apiClient.fetchCategories();

        assertThat(categories).hasSize(2);
        assertThat(categories[0].name()).isEqualTo("Art");
        assertThat(categories[1].name()).isEqualTo("Music");
    }

    @Test
    void testFetchCategoriesError() {
        WireMock.configureFor(wiremockServer.getHost(), wiremockServer.getFirstMappedPort());
        stubFor(get(urlEqualTo(categoriesEndpoint))
                .willReturn(aResponse()
                        .withStatus(500)));

        Category[] categories = apiClient.fetchCategories();

        assertThat(categories).isNull();
    }

    @Test
    void testFetchLocationsSuccess() {
        WireMock.configureFor(wiremockServer.getHost(), wiremockServer.getFirstMappedPort());
        String locationsJson = "[{\"slug\":\"moscow\",\"name\":\"Moscow\"},{\"slug\":\"spb\",\"name\":\"Saint Petersburg\"}]";
        stubFor(get(urlEqualTo(locationsEndpoint))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(locationsJson)));
        Location[] locations = apiClient.fetchLocations();

        assertThat(locations).hasSize(2);
        assertThat(locations[0].name()).isEqualTo("Moscow");
        assertThat(locations[1].name()).isEqualTo("Saint Petersburg");
    }

    @Test
    void testFetchLocationsError() {
        WireMock.configureFor(wiremockServer.getHost(), wiremockServer.getFirstMappedPort());
        stubFor(get(urlEqualTo(locationsEndpoint))
                .willReturn(aResponse()
                        .withStatus(500)));

        Location[] locations = apiClient.fetchLocations();

        assertThat(locations).isNull();
    }
}
