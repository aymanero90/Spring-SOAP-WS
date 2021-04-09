package org.currencyconverter.api.service;

import org.currencyconverter.api.converter.ConversionRequest;
import org.currencyconverter.api.converter.ConversionResponse;
import org.currencyconverter.api.converter.CurrencyListRequest;
import org.currencyconverter.api.converter.CurrencyListResponse;

public interface CurrencyConverterService {
    ConversionResponse calculateRate(ConversionRequest request);

    CurrencyListResponse getCurrencyList();
}
