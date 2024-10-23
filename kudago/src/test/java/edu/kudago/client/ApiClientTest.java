package edu.kudago.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import edu.kudago.dto.Category;
import edu.kudago.dto.Event;
import edu.kudago.dto.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApiClientTest {

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0");

    @Autowired
    private ApiClient apiClient;

    @Value("${kudago.categories-endpoint}")
    private String categoriesEndpoint;

    @Value("${kudago.locations-endpoint}")
    private String locationsEndpoint;

    @Value("${kudago.events-endpoint}")
    private String eventsEndpoint;

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("api.base-url", wiremockServer::getBaseUrl);
    }

    @BeforeEach
    public void setup() {
        WireMock.configureFor(wiremockServer.getHost(), wiremockServer.getFirstMappedPort());
    }

    @AfterEach
    public void teardown() {
        WireMock.reset();
    }

    @Test
    void testFetchCategoriesSuccess() {
        String categoriesJson = "[{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"},{\"id\":2,\"slug\":\"music\",\"name\":\"Music\"}]";
        stubForGetRequest(categoriesEndpoint, categoriesJson, 200);

        Flux<Category> categoryFlux = apiClient.fetchCategories();

        StepVerifier.create(categoryFlux)
                .expectNextMatches(category -> category.name().equals("Art"))
                .expectNextMatches(category -> category.name().equals("Music"))
                .verifyComplete();
    }

    @Test
    void testFetchCategoriesError() {
        stubForErrorResponse(categoriesEndpoint, 500);

        StepVerifier.create(apiClient.fetchCategories())
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getRawStatusCode() == 500)
                .verify();
    }

    @Test
    void testFetchLocationsSuccess() {
        String locationsJson = "[{\"slug\":\"moscow\",\"name\":\"Moscow\"},{\"slug\":\"spb\",\"name\":\"Saint Petersburg\"}]";
        stubForGetRequest(locationsEndpoint, locationsJson, 200);

        Flux<Location> locationFlux = apiClient.fetchLocations();

        StepVerifier.create(locationFlux)
                .expectNextMatches(location -> location.name().equals("Moscow"))
                .expectNextMatches(location -> location.name().equals("Saint Petersburg"))
                .verifyComplete();
    }

    @Test
    void testFetchLocationsError() {
        stubForErrorResponse(locationsEndpoint, 500);

        StepVerifier.create(apiClient.fetchLocations())
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getRawStatusCode() == 500)
                .verify();
    }

    @Test
    void testGetEventsBetweenDatesSuccess() {
        long startDate = 1672531200L;
        long endDate = 1672617600L;

        String eventsJson = "{ \"results\": [" +
                "{\"id\": 1, \"title\": \"Event 1\", \"price\": \"Free\"}," +
                "{\"id\": 2, \"title\": \"Event 2\", \"price\": \"100 RUB\"}" +
                "] }";
        stubForGetEventsRequest(startDate, endDate, eventsJson, 200);

        Flux<Event> eventFlux = apiClient.getEventsBetweenDates(startDate, endDate);

        StepVerifier.create(eventFlux)
                .expectNextMatches(event -> event.title().equals("Event 1"))
                .expectNextMatches(event -> event.title().equals("Event 2"))
                .verifyComplete();
    }

    @Test
    void testGetEventsBetweenDatesError() {
        long startDate = 1672531200L;
        long endDate = 1672617600L;

        stubForGetEventsRequest(startDate, endDate, "", 500);

        StepVerifier.create(apiClient.getEventsBetweenDates(startDate, endDate))
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getRawStatusCode() == 500)
                .verify();
    }

    @Test
    void testGetEventsBetweenDatesSyncSuccess() {
        long startDate = 1672531200L;
        long endDate = 1672617600L;

        String eventsJson = "{ \"results\": [" +
                "{\"id\": 1, \"title\": \"Event 1\", \"price\": \"Free\"}," +
                "{\"id\": 2, \"title\": \"Event 2\", \"price\": \"100 RUB\"}" +
                "] }";
        stubForGetEventsRequest(startDate, endDate, eventsJson, 200);

        List<Event> events = apiClient.getEventsBetweenDatesSync(startDate, endDate);

        assertThat(events).hasSize(2);
        assertThat(events.get(0).title()).isEqualTo("Event 1");
        assertThat(events.get(1).title()).isEqualTo("Event 2");
    }

    @Test
    void testGetEventsBetweenDatesSyncError() {
        long startDate = 1672531200L;
        long endDate = 1672617600L;

        stubForGetEventsRequest(startDate, endDate, "", 500);

        assertThatThrownBy(() -> apiClient.getEventsBetweenDatesSync(startDate, endDate))
                .isInstanceOf(WebClientResponseException.InternalServerError.class)
                .hasMessageContaining("500 Internal Server Error");
    }

    @Test
    @Timeout(10)
    void testRateLimiting() throws InterruptedException {
        String categoriesJson = "[{\"id\":1,\"slug\":\"art\",\"name\":\"Art\"}]";
        stubForGetRequest(categoriesEndpoint, categoriesJson, 200);

        AtomicInteger counter = new AtomicInteger(0);

        Flux<Category> categoryFlux = Flux.range(1, 10)
                .flatMap(i -> apiClient.fetchCategories()
                        .doOnNext(category -> counter.incrementAndGet())
                );

        StepVerifier.create(categoryFlux)
                .expectNextCount(10)
                .verifyComplete();

        assertThat(counter.get()).isEqualTo(10);
    }

    private void stubForGetRequest(String endpoint, String responseJson, int status) {
        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson)));
    }

    private void stubForErrorResponse(String endpoint, int status) {
        stubFor(get(urlEqualTo(endpoint))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));
    }

    private void stubForGetEventsRequest(long startDate, long endDate, String responseJson, int status) {
        stubFor(get(urlPathEqualTo(eventsEndpoint))
                .withQueryParam("actual_since", equalTo(String.valueOf(startDate)))
                .withQueryParam("actual_until", equalTo(String.valueOf(endDate)))
                .withQueryParam("fields", equalTo("id,title,price"))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseJson)));
    }
}
