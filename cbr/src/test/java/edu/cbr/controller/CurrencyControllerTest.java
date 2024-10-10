package edu.cbr.controller;

import edu.cbr.dto.ConvertRequest;
import edu.cbr.dto.ConvertResponse;
import edu.cbr.dto.CurrencyRateResponse;
import edu.cbr.exceptions.CurrencyDoesntExistException;
import edu.cbr.exceptions.CurrencyNotFoundException;
import edu.cbr.exceptions.CurrencyServiceUnavailableException;
import edu.cbr.service.CurrencyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/currencies";

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "CNY", "AUD"})
    @DisplayName("Получить курс валюты: валидный код валюты возвращает статус 200 (OK)")
    public void getCurrencyRate_ValidCode_ReturnsOk(String code) throws Exception {
        when(currencyService.getCurrencyRate(code))
                .thenReturn(new CurrencyRateResponse(code, 95.0));

        mockMvc.perform(get(BASE_URL + "/rates/{code}", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(code))
                .andExpect(jsonPath("$.rate").value(95.0))
                .andDo(print());
    }

    @Test
    @DisplayName("Получить курс валюты: некорректный код валюты возвращает статус 404 (Not Found)")
    public void getCurrencyRate_InvalidCode_ReturnsNotFound() throws Exception {
        when(currencyService.getCurrencyRate("INV"))
                .thenThrow(new CurrencyNotFoundException("Currency not found: INV"));

        mockMvc.perform(get(BASE_URL + "/rates/{code}", "INV"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Currency not found: INV"))
                .andDo(print());
    }

    @Test
    @DisplayName("Конвертация валюты: валидный запрос возвращает статус 200 (OK)")
    public void convertCurrency_ValidRequest_ReturnsOk() throws Exception {
        ConvertResponse response = new ConvertResponse("USD", "RUB", 7600.0);

        when(currencyService.convertCurrency(any(ConvertRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\",\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("RUB"))
                .andExpect(jsonPath("$.convertedAmount").value(7600.0))
                .andDo(print());
    }

    @Test
    @DisplayName("Конвертация валюты: сумма должна быть больше нуля, возвращает статус 400 (Bad Request)")
    public void convertCurrency_InvalidAmount_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\",\"amount\":-100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST: amount - The amount for conversion must be a positive number"))
                .andDo(print());
    }

    @Test
    @DisplayName("Конвертация валюты: код валюты 'from' недопустим, возвращает статус 400 (Bad Request)")
    public void convertCurrency_InvalidFromCurrency_ReturnsBadRequest() throws Exception {
        when(currencyService.convertCurrency(any(ConvertRequest.class)))
                .thenThrow(new CurrencyDoesntExistException("400 BAD_REQUEST: fromCurrency - The currency code must contain exactly 3 characters"));

        mockMvc.perform(post(BASE_URL + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCurrency\":\"INVALID\",\"toCurrency\":\"RUB\",\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST: fromCurrency - The currency code must contain exactly 3 characters"))
                .andDo(print());
    }

    @Test
    @DisplayName("Конвертация валюты: код валюты 'to' недопустим, возвращает статус 400 (Bad Request)")
    public void convertCurrency_InvalidToCurrency_ReturnsBadRequest() throws Exception {
        when(currencyService.convertCurrency(any(ConvertRequest.class)))
                .thenThrow(new CurrencyDoesntExistException("400 BAD_REQUEST: toCurrency - The currency code must contain exactly 3 characters"));

        mockMvc.perform(post(BASE_URL + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCurrency\":\"USD\",\"toCurrency\":\"INVALID\",\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST: toCurrency - The currency code must contain exactly 3 characters"))
                .andDo(print());
    }


    @Test
    @DisplayName("Ошибка при недоступности сервиса ЦБ возвращает статус 503 (Service Unavailable)")
    public void convertCurrency_CbrServiceUnavailable_ReturnsServiceUnavailable() throws Exception {
        when(currencyService.convertCurrency(any(ConvertRequest.class)))
                .thenThrow(new CurrencyServiceUnavailableException("Currency service is unavailable"));

        mockMvc.perform(post(BASE_URL + "/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\",\"amount\":100}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Currency service is unavailable"))
                .andDo(print());
    }
}
