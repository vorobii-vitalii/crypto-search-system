package org.vitalii.vorobii.service;

import com.example.crypto_search.Crypto;
import com.example.crypto_search.CryptoSearchRequest;

import reactor.core.publisher.Flux;

public interface CryptoSearchService {
	Flux<Crypto> findCryptoCurrencies(CryptoSearchRequest cryptoSearchRequest);
}
