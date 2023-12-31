package org.vitalii.vorobii.utils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;

class TestIndexCreator {
	private static final String INDEX_NAME = "index123";

	ElasticsearchClient elasticsearchClient = mock(ElasticsearchClient.class);

	ElasticsearchIndicesClient elasticsearchIndicesClient = mock(ElasticsearchIndicesClient.class);

	IndexCreator indexCreator = new IndexCreator(elasticsearchClient);

	@SuppressWarnings("unchecked")
	@Test
	void createIndexGivenIndexAlreadyExists() throws IOException {
		when(elasticsearchClient.indices()).thenReturn(elasticsearchIndicesClient);
		when(elasticsearchIndicesClient.exists(any(ExistsRequest.class))).thenReturn(new BooleanResponse(true));
		indexCreator.createIndex(INDEX_NAME);
		verify(elasticsearchIndicesClient, never()).create(any(Function.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	void createIndexGivenIndexNotExists() throws IOException {
		when(elasticsearchClient.indices()).thenReturn(elasticsearchIndicesClient);
		when(elasticsearchIndicesClient.exists(any(ExistsRequest.class))).thenReturn(new BooleanResponse(false));
		indexCreator.createIndex(INDEX_NAME);
		verify(elasticsearchIndicesClient).create(any(Function.class));
	}

}
