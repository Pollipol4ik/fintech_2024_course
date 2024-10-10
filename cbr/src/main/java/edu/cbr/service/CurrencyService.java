package edu.cbr.service;

import edu.cbr.client.CbrClient;
import edu.cbr.dto.ConvertRequest;
import edu.cbr.dto.ConvertResponse;
import edu.cbr.dto.CurrencyRateResponse;
import edu.cbr.entity.Valute;
import edu.cbr.exceptions.CurrencyDoesntExistException;
import edu.cbr.exceptions.CurrencyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CurrencyService {

    private final CbrClient cbrClient;

    @Cacheable(value = "dailyRates")
    public List<Valute> getCachedDailyRates() {
        return cbrClient.getDailyRates();
    }

    public CurrencyRateResponse getCurrencyRate(String code) {
        List<Valute> rates = getCachedDailyRates();
        Valute valute = rates.stream()
                .filter(v -> v.getCharCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + code));

        return new CurrencyRateResponse(valute.getCharCode(), Double.parseDouble(valute.getValue().replace(",", ".")));
    }

    public ConvertResponse convertCurrency(ConvertRequest request) {
        List<Valute> rates = getCachedDailyRates();
        double fromRate = getRate(request.fromCurrency(), rates);
        double toRate = getRate(request.toCurrency(), rates);
        double convertedAmount = request.amount() * (fromRate / toRate);

        log.debug("From currency with rate: {} and code: {}", fromRate, request.fromCurrency());
        log.debug("To currency with rate: {} and code: {}", toRate, request.toCurrency());
        log.debug("Converted amount: {}", convertedAmount);

        return new ConvertResponse(request.fromCurrency(), request.toCurrency(), convertedAmount);
    }

    private double getRate(String currencyCode, List<Valute> rates) {
        if ("RUB".equalsIgnoreCase(currencyCode)) {
            return 1.0;
        }
        return rates.stream()
                .filter(valute -> valute.getCharCode().equalsIgnoreCase(currencyCode))
                .map(valute -> Double.parseDouble(valute.getValue().replace(",", ".")))
                .findFirst()
                .orElseThrow(() -> new CurrencyDoesntExistException("Unsupported currency code: " + currencyCode));
    }
}
