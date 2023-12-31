package org.vitalii.vorobii.service.impl;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vitalii.vorobii.dto.CryptoCurrency;
import org.vitalii.vorobii.service.CryptoCurrenciesService;

import reactor.test.StepVerifier;

class TestFinnHubCryptoCurrenciesService {
	private static final String API_KEY = "arkhormh";
	private static final String PROTOCOL = "http";
	private static final String HOST_PORT = "host:82";
	public static final String GET_MARKETS_URL = "http://host:82/api/v1/crypto/exchange/?token=arkhormh";
	public static final String MARKET_1_URL = "http://host:82/api/v1/crypto/symbol?exchange=m1&token=arkhormh";
	public static final String MARKET_2_URL = "http://host:82/api/v1/crypto/symbol?exchange=m2&token=arkhormh";
	private static final String MARKET_1 = "M1";
	private static final String MARKET_2 = "M2";
	private static final List<String> MARKET_IDS = List.of(MARKET_1, MARKET_2);

	HttpClient httpClient = mock(HttpClient.class);

	CryptoCurrenciesService cryptoCurrenciesService = new FinnHubCryptoCurrenciesService(httpClient, API_KEY, PROTOCOL, HOST_PORT);

	@SuppressWarnings("unchecked")
	@Test
	void getAllCryptoCurrencies() {
		HttpResponse<Supplier<List<String>>> marketIdsResponse = mock(HttpResponse.class);
		when(marketIdsResponse.body()).thenReturn(() -> MARKET_IDS);
		when(httpClient.sendAsync(
				eq(HttpRequest.newBuilder().GET().uri(URI.create(GET_MARKETS_URL)).build()),
				Mockito.<HttpResponse.BodyHandler<Supplier<List<String>>>> any()
		)).thenReturn(CompletableFuture.completedFuture(marketIdsResponse));

		HttpResponse<Supplier<List<FinnHubCryptoCurrenciesService.CryptoSymbol>>> cryptoSymbolsResponse1 = mock(HttpResponse.class);
		when(cryptoSymbolsResponse1.body()).thenReturn(() -> List.of(
				new FinnHubCryptoCurrenciesService.CryptoSymbol("Bitcoin", "XBT", "XBT"),
				new FinnHubCryptoCurrenciesService.CryptoSymbol("LTCoin", "LTC", "LTC")
		));
		HttpResponse<Supplier<List<FinnHubCryptoCurrenciesService.CryptoSymbol>>> cryptoSymbolsResponse2 = mock(HttpResponse.class);
		when(cryptoSymbolsResponse2.body()).thenReturn(() -> List.of(
			new FinnHubCryptoCurrenciesService.CryptoSymbol("Bitcoin", "XBT", "XBT")
		));
		when(httpClient.sendAsync(
				eq(HttpRequest.newBuilder().GET().uri(URI.create(MARKET_1_URL)).build()),
				Mockito.<HttpResponse.BodyHandler<Supplier<List<FinnHubCryptoCurrenciesService.CryptoSymbol>>>> any()
		)).thenReturn(CompletableFuture.completedFuture(cryptoSymbolsResponse1));
		when(httpClient.sendAsync(
				eq(HttpRequest.newBuilder().GET().uri(URI.create(MARKET_2_URL)).build()),
				Mockito.<HttpResponse.BodyHandler<Supplier<List<FinnHubCryptoCurrenciesService.CryptoSymbol>>>> any()
		)).thenReturn(CompletableFuture.completedFuture(cryptoSymbolsResponse2));

		StepVerifier.create(cryptoCurrenciesService.getAllCryptoCurrencies())
				.expectNext(new CryptoCurrency("M1", "XBT", "Bitcoin"))
				.expectNext(new CryptoCurrency("M1", "LTC", "LTCoin"))
				.expectNext(new CryptoCurrency("M2", "XBT", "Bitcoin"))
				.expectComplete()
				.log()
				.verify();

	}

}
