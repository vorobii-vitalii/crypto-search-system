package org.vitalii.vorobii.service.impl;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.dto.CryptoCurrency;
import org.vitalii.vorobii.service.CryptoSearchService;

import com.example.crypto_search.Crypto;
import com.example.crypto_search.CryptoSearchRequest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import reactor.core.publisher.Flux;

public class ElasticSearchCryptoSearchService implements CryptoSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchCryptoSearchService.class);

	private final ElasticsearchClient elasticsearchClient;
	private final String indexName;

	public ElasticSearchCryptoSearchService(
			ElasticsearchClient elasticsearchClient,
			String indexName
	) {
		this.elasticsearchClient = elasticsearchClient;
		this.indexName = indexName;
	}

	@Override
	public Flux<Crypto> findCryptoCurrencies(CryptoSearchRequest cryptoSearchRequest) {
		return Flux.create(sink -> {
			try {
				var searchRequest = SearchRequest.of(b ->
						b.index(indexName)
								.query(q -> q
										.match(t -> t.field("description").query(cryptoSearchRequest.getQuery()))));
				var searchResponse =
						elasticsearchClient.search(
								searchRequest,
								CryptoCurrency.class);
				LOGGER.info("Search response on request {} = {}", searchRequest, searchResponse);
				searchResponse.hits()
						.hits()
						.stream()
						.map(Hit::source)
						.filter(Objects::nonNull)
						.forEach(v -> sink.next(toInternalDTO(v)));
				sink.complete();
			}
			catch (IOException e) {
				LOGGER.error("Error occurred on search", e);
				sink.error(e);
			}
		});
	}

	private Crypto toInternalDTO(CryptoCurrency cryptoCurrency) {
		return Crypto.newBuilder()
				.setSymbol(cryptoCurrency.symbol())
				.setDescription(cryptoCurrency.description())
				.setMarketName(cryptoCurrency.marketName())
				.build();
	}
}
