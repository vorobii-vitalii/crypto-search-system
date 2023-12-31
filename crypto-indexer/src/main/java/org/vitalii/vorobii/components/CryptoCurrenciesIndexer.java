package org.vitalii.vorobii.components;

import javax.inject.Named;

import org.vitalii.vorobii.constants.IndexingConstants;
import org.vitalii.vorobii.indexing.CryptoIndexer;
import org.vitalii.vorobii.modules.ElasticSearchModule;
import org.vitalii.vorobii.modules.FinnHubModule;
import org.vitalii.vorobii.utils.IndexCreator;

import dagger.Component;

@Component(modules = {FinnHubModule.class, ElasticSearchModule.class})
public interface CryptoCurrenciesIndexer {
	CryptoIndexer cryptoIndexer();
	IndexCreator indexCreator();
	@Named(IndexingConstants.INDEX_NAME)
	String getIndexName();
}
