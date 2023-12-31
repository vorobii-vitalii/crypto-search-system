package org.vitalii.vorobii.indexing;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.constants.IndexingConstants;
import org.vitalii.vorobii.service.CryptoCurrenciesService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import reactor.core.publisher.Flux;

public class CryptoIndexer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoIndexer.class);
	protected static final int BUFFER_SIZE = 50;

	private final CryptoCurrenciesService cryptoCurrenciesService;
	private final ElasticsearchClient elasticsearchClient;
	private final String indexName;

	@Inject
	public CryptoIndexer(
			CryptoCurrenciesService cryptoCurrenciesService,
			ElasticsearchClient elasticsearchClient,
			@Named(IndexingConstants.INDEX_NAME) String indexName
	) {
		this.cryptoCurrenciesService = cryptoCurrenciesService;
		this.elasticsearchClient = elasticsearchClient;
		this.indexName = indexName;
	}

	public void indexAllCryptoCurrencies() {
		LOGGER.info("Starting indexing of crypto currencies into {}", indexName);
		cryptoCurrenciesService.getAllCryptoCurrencies()
				.buffer(BUFFER_SIZE)
				.switchIfEmpty(Flux.empty())
				.handle((cryptoCurrencies, sink) -> {
					LOGGER.info("Going to index {}", cryptoCurrencies);
					var bulkRequest = cryptoCurrencies.stream()
							.reduce(new BulkRequest.Builder(),
									(b, obj) -> b.operations(op -> op.index(idx -> idx.index(indexName).id(obj.getId()).document(obj))),
									(a, b) -> b)
							.build();
					sink.next(cryptoCurrencies.size());
					try {
						var result = elasticsearchClient.bulk(bulkRequest);
						LOGGER.info("Index operation took {} ms", result.took());
						if (result.errors()) {
							LOGGER.error("Indexing had errors");
							result.items().stream()
									.filter(v -> v.error() != null)
									.forEach(v -> LOGGER.error(v.error().reason()));
						}
					}
					catch (IOException e) {
						sink.error(e);
					}
				})
				.blockLast();
	}

}
