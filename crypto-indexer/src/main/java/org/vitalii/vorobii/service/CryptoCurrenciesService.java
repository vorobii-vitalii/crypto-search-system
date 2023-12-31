package org.vitalii.vorobii.service;

import org.vitalii.vorobii.dto.CryptoCurrency;

import reactor.core.publisher.Flux;

public interface CryptoCurrenciesService {
	Flux<CryptoCurrency> getAllCryptoCurrencies();
}
