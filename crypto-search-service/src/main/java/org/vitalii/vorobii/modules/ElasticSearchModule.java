package org.vitalii.vorobii.modules;

import java.io.IOException;
import java.util.List;

import javax.inject.Named;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.constants.IndexingConstants;
import org.vitalii.vorobii.service.CryptoSearchService;
import org.vitalii.vorobii.service.impl.ElasticSearchCryptoSearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.util.ContentType;
import dagger.Module;
import dagger.Provides;

@Module
public class ElasticSearchModule {
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchModule.class);

	@Provides
	CryptoSearchService cryptoSearchService(ElasticsearchClient elasticsearchClient, @Named(IndexingConstants.INDEX_NAME) String indexName) {
		return new ElasticSearchCryptoSearchService(elasticsearchClient, indexName);
	}

	@Provides
	ElasticsearchClient elasticsearchClient(ElasticsearchTransport elasticsearchTransport) {
		return new ElasticsearchClient(elasticsearchTransport);
	}

	@Provides
	ElasticsearchTransport elasticsearchTransport(@Named(IndexingConstants.ELASTIC_URL) String elasticURL) {
		LOGGER.info("Creating connection to {}", elasticURL);
		var restClient = RestClient
				.builder(HttpHost.create(elasticURL))
				.setHttpClientConfigCallback(httpClientBuilder -> {
					httpClientBuilder.disableAuthCaching();
					httpClientBuilder.setDefaultHeaders(List.of(
							new BasicHeader(
									HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)));
					httpClientBuilder.addInterceptorLast((HttpResponseInterceptor)
							(response, context) ->
									response.addHeader("X-Elastic-Product", "Elasticsearch"));
					return httpClientBuilder;
				})
				.build();
		var restClientTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				restClientTransport.close();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		return restClientTransport;
	}

	@Named(IndexingConstants.ELASTIC_URL)
	@Provides
	public String getElasticURL() {
		return System.getenv(IndexingConstants.ELASTIC_URL);
	}

	@Named(IndexingConstants.INDEX_NAME)
	@Provides
	public String getIndexName() {
		return System.getenv(IndexingConstants.INDEX_NAME);
	}

}
