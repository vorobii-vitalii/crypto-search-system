package org.vitalii.vorobii.service.impl;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.vitalii.vorobii.dto.CryptoCurrency;

import com.example.crypto_search.Crypto;
import com.example.crypto_search.CryptoSearchRequest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import reactor.test.StepVerifier;

class TestElasticSearchCryptoSearchService {
	private static final String INDEX_NAME = "cryptos";

	ElasticsearchClient elasticsearchClient = mock(ElasticsearchClient.class);

	ElasticSearchCryptoSearchService searchCryptoSearchService = new ElasticSearchCryptoSearchService(elasticsearchClient, INDEX_NAME);

	@SuppressWarnings("unchecked")
	@Test
	void findCryptoCurrencies() throws IOException {
		SearchResponse<CryptoCurrency> searchResponse = mock(SearchResponse.class);
		HitsMetadata<CryptoCurrency> hitsMetadata = mock(HitsMetadata.class);
		when(elasticsearchClient.search(
				argThat((SearchRequest request) -> request.index().equals(List.of(INDEX_NAME))),
				eq(CryptoCurrency.class)
		)).thenReturn(searchResponse);
		when(searchResponse.hits()).thenReturn(hitsMetadata);
		List<Hit<CryptoCurrency>> hits = List.of(
			create(new CryptoCurrency("M1", "S1", "D1")),
			create(new CryptoCurrency("M1", "S2", "D2")),
			create(new CryptoCurrency("M2", "S1", "D1"))
		);
		when(hitsMetadata.hits()).thenReturn(hits);
		StepVerifier.create(searchCryptoSearchService.findCryptoCurrencies(CryptoSearchRequest.newBuilder().setQuery("query").build()))
				.expectNext(Crypto.newBuilder().setSymbol("S1").setDescription("D1").setMarketName("M1").build())
				.expectNext(Crypto.newBuilder().setSymbol("S2").setDescription("D2").setMarketName("M1").build())
				.expectNext(Crypto.newBuilder().setSymbol("S1").setDescription("D1").setMarketName("M2").build())
				.expectComplete()
				.log()
				.verify();

	}

	@SuppressWarnings("unchecked")
	private Hit<CryptoCurrency> create(CryptoCurrency cryptoCurrency) {
		Hit<CryptoCurrency> hit = mock(Hit.class);
		when(hit.source()).thenReturn(cryptoCurrency);
		return hit;
	}

}
