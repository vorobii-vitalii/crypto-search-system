package org.vitalii.vorobii.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.dto.CryptoCurrency;
import org.vitalii.vorobii.service.CryptoCurrenciesService;
import org.vitalii.vorobii.utils.JacksonBodyHandler;

import com.fasterxml.jackson.core.type.TypeReference;

import co.elastic.apm.api.CaptureSpan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FinnHubCryptoCurrenciesService implements CryptoCurrenciesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FinnHubCryptoCurrenciesService.class);

	private final HttpClient httpClient;
	private final String apiKey;
	private final String protocol;
	private final String hostPort;

	public FinnHubCryptoCurrenciesService(HttpClient httpClient, String apiKey, String protocol, String hostPort) {
		this.httpClient = httpClient;
		this.apiKey = apiKey;
		this.protocol = protocol;
		this.hostPort = hostPort;
	}

	@CaptureSpan
	@Override
	public Flux<CryptoCurrency> getAllCryptoCurrencies() {
		return Mono.fromFuture(httpClient.sendAsync(createGetMarketsRequest(), new JacksonBodyHandler<>(new TypeReference<List<String>>() {
				})))
				.map(HttpResponse::body)
				.flatMapMany(marketIdsSupplier -> {
					var marketIds = marketIdsSupplier.get();
					LOGGER.info("Fetched marketIds = {}", marketIds);
					return Flux.fromIterable(marketIds)
							.flatMap(marketId -> {
								LOGGER.info("Fetching symbols by market = {}", marketId);
								return Mono.fromFuture(httpClient.sendAsync(
												createGetCryptoSymbolsRequest(marketId),
												new JacksonBodyHandler<>(
														new TypeReference<List<CryptoSymbol>>() {
														})))
										.flatMapMany(v -> Flux.fromIterable(v.body().get()))
										.map(cryptoSymbol -> {
											LOGGER.info("Found crypto currency {}", cryptoSymbol);
											return new CryptoCurrency(marketId, cryptoSymbol.displaySymbol, cryptoSymbol.description);
										});
							});
				});
	}

	public record CryptoSymbol(String description, String displaySymbol, String symbol) {
	}

	private HttpRequest createGetMarketsRequest() {
		return HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(protocol + "://" + hostPort + "/api/v1/crypto/exchange/?token=" + apiKey))
				.build();
	}

	private HttpRequest createGetCryptoSymbolsRequest(String exchange) {
		return HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(protocol + "://" + hostPort + "/api/v1/crypto/symbol?exchange=" + exchange.toLowerCase() + "&token=" + apiKey))
				.build();
	}
}
