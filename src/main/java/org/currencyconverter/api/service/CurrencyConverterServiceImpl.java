package org.currencyconverter.api.service;

import org.apache.commons.math3.util.Precision;
import org.currencyconverter.api.converter.ConversionRequest;
import org.currencyconverter.api.converter.ConversionResponse;
import org.currencyconverter.api.converter.CurrencyListResponse;
import org.currencyconverter.api.currencyretrieval.CurrencyRetrieval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class CurrencyConverterServiceImpl implements CurrencyConverterService {
    private static final String EUR = "EUR";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyConverterServiceImpl.class);

    @Override
    public ConversionResponse calculateRate(ConversionRequest request) {
        ConversionResponse ack = new ConversionResponse();

        if (request.getFromCurrency() == null || request.getToCurrency() == null) {
            LOGGER.error("Currency is missing!");
            ack.setRate(0);
            return ack;
        }

        Map<String, Double> map = CurrencyRetrieval.retrieveCurrencyRates();
        if (map == null || map.isEmpty()) {
            LOGGER.error("No currencies were retrieved");
            ack.setRate(0);
            return ack;
        }

        double euroToCurrencyRate = 1.0;
        double currencyToEuroRate = 1.0;
        String fromCurrency = request.getFromCurrency().toUpperCase();
        String toCurrency = request.getToCurrency().toUpperCase();

        if (!EUR.equals(fromCurrency)) {  // If Currency is Euro rate still 1.0
            if (map.containsKey(fromCurrency)) {   // Currency is not Euro but in the list => get the rate
                currencyToEuroRate = map.get(fromCurrency);
            } else {    // Currency is neither Euro and nor in the list => rate not found
                LOGGER.info("The source currency {} is not found!", fromCurrency);
                ack.setRate(0);
                return ack;
            }
        }

        if (!EUR.equals(toCurrency)) {   // Same logic as above
            if (map.containsKey(toCurrency)) {
                euroToCurrencyRate = map.get(toCurrency);
            } else {
                LOGGER.info("The target currency {} is not found!", toCurrency);
                ack.setRate(0);
                return ack;
            }
        }

        ack.setRate(Precision.round(euroToCurrencyRate / currencyToEuroRate, 5));
        return ack;
    }


    @Override
    public CurrencyListResponse getCurrencyList() {

        CurrencyListResponse ack = new CurrencyListResponse();
        List<String> currencyList = CurrencyRetrieval.retrieveCurrencyList();

        if (currencyList == null || currencyList.isEmpty()) {
            LOGGER.error("No currencies were retrieved");
            return ack;
        }
        ack.getCurrencyList().addAll(currencyList);
        return ack;
    }
}
