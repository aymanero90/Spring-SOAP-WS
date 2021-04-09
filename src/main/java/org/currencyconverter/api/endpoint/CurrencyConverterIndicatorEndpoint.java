package org.currencyconverter.api.endpoint;

import org.currencyconverter.api.config.CurrencyConverterConfig;
import org.currencyconverter.api.converter.ConversionRequest;
import org.currencyconverter.api.converter.ConversionResponse;
import org.currencyconverter.api.converter.CurrencyListResponse;
import org.currencyconverter.api.service.CurrencyConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.validation.constraints.NotNull;


@Endpoint  // Presentation layer
public class CurrencyConverterIndicatorEndpoint {

    private final CurrencyConverterService service;

    @Autowired
    public CurrencyConverterIndicatorEndpoint(CurrencyConverterService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = CurrencyConverterConfig.CURRENCY_CONVERTER_URL, localPart = "ConversionRequest")
    @ResponsePayload
    public ConversionResponse calculateCurrencyRate(@RequestPayload @NotNull ConversionRequest request) {
        return service.calculateRate(request);
    }



    @PayloadRoot(namespace = CurrencyConverterConfig.CURRENCY_CONVERTER_URL, localPart = "CurrencyListRequest")
    @ResponsePayload
    public CurrencyListResponse getCurrencyList() {
        return service.getCurrencyList();
    }
}
