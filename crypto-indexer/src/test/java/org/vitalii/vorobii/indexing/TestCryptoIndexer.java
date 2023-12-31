package org.vitalii.vorobii.indexing;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.vitalii.vorobii.indexing.CryptoIndexer.BUFFER_SIZE;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.vitalii.vorobii.dto.CryptoCurrency;
import org.vitalii.vorobii.service.CryptoCurrenciesService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.OperationType;
import reactor.core.publisher.Flux;

class TestCryptoIndexer {
	private static final String INDEX_NAME = "cryptos";

	CryptoCurrenciesService cryptoCurrenciesService = mock(CryptoCurrenciesService.class);
	ElasticsearchClient elasticsearchClient = mock(ElasticsearchClient.class);

	CryptoIndexer cryptoIndexer = new CryptoIndexer(cryptoCurrenciesService, elasticsearchClient, INDEX_NAME);

	private static final int N = 500;

	@Test
	void indexAllCryptoCurrenciesHappyPath() throws IOException {
		when(cryptoCurrenciesService.getAllCryptoCurrencies()).thenReturn(createCryptos(N));
		when(elasticsearchClient.bulk(any(BulkRequest.class)))
				.thenReturn(BulkResponse.of(v -> v.took(100L).errors(false).items(List.of())));
		cryptoIndexer.indexAllCryptoCurrencies();
		verify(elasticsearchClient, times((int) Math.ceil((double)N / BUFFER_SIZE))).bulk(any(BulkRequest.class));
	}

	@Test
	void indexAllCryptoCurrenciesSomeCryptosCouldNotBeSynchronized() throws IOException {
		when(cryptoCurrenciesService.getAllCryptoCurrencies()).thenReturn(createCryptos(N));
		when(elasticsearchClient.bulk(any(BulkRequest.class)))
				.thenReturn(BulkResponse.of(v -> v.took(100L)
						.errors(true)
						.items(List.of(
								BulkResponseItem.of(i -> i.operationType(OperationType.Index)
										.index(INDEX_NAME)
										.status(1)
										.error(ErrorCause.of(e -> e)))
						))));
		cryptoIndexer.indexAllCryptoCurrencies();
		verify(elasticsearchClient, times((int) Math.ceil((double)N / BUFFER_SIZE))).bulk(any(BulkRequest.class));
	}

	@Test
	void indexAllCryptoCurrenciesCriticalErrorOccurredWhenIndexing() throws IOException {
		when(cryptoCurrenciesService.getAllCryptoCurrencies()).thenReturn(createCryptos(10));
		when(elasticsearchClient.bulk(any(BulkRequest.class)))
				.thenThrow(new IOException());
		assertThrows(Exception.class, () -> cryptoIndexer.indexAllCryptoCurrencies());
	}

	private Flux<CryptoCurrency> createCryptos(int n) {
		return Flux.fromStream(IntStream.rangeClosed(1, n)
				.boxed()
				.map(v -> new CryptoCurrency(v.toString(), v.toString(), v.toString())));
	}

}
