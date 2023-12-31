package org.vitalii.vorobii.utils;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;

public class IndexCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexCreator.class);

	private final ElasticsearchClient client;

	@Inject
	public IndexCreator(ElasticsearchClient client) {
		this.client = client;
	}

	public void createIndex(String indexName) throws IOException {
		LOGGER.info("Trying to create index {}", indexName);
		var isIndexExists = Optional.ofNullable(client.indices().exists(ExistsRequest.of(e -> e.index(indexName))))
				.map(BooleanResponse::value)
				.orElse(false);
		if (isIndexExists) {
			LOGGER.info("Index {} already exists", indexName);
		} else {
			LOGGER.info("Index {} doesnt exist, creating...", indexName);
			var isIndexCreateAcknowledged = Optional.ofNullable(client.indices().create(b -> b.index(indexName)))
					.map(CreateIndexResponse::acknowledged)
					.orElse(false);
			LOGGER.info("Index {} successfully created, acknowledged = {}", indexName, isIndexCreateAcknowledged);
		}
	}

}
