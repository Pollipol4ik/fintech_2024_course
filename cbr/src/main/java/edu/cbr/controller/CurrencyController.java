package edu.cbr.controller;

import edu.cbr.dto.ConvertRequest;
import edu.cbr.dto.ConvertResponse;
import edu.cbr.dto.CurrencyRateResponse;
import edu.cbr.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(summary = "Get currency rate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Currency rate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Currency not found")
    })
    @GetMapping("/rates/{code}")
    public CurrencyRateResponse getCurrencyRate(@PathVariable String code) {
        return currencyService.getCurrencyRate(code);
    }

    @Operation(summary = "Convert currency")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Currency converted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "404", description = "Currency not found"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PostMapping("/convert")
    public ConvertResponse convertCurrency(@Valid @RequestBody ConvertRequest request) {
        return currencyService.convertCurrency(request);
    }
}
