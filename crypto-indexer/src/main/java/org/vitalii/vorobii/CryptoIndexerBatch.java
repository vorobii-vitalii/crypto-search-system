package org.vitalii.vorobii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.components.DaggerCryptoCurrenciesIndexer;

public class CryptoIndexerBatch {
	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoIndexerBatch.class);
	private static final int SUCCESS_STATUS = 0;
	private static final int ERROR_STATUS = 1;

	public static void main(String[] args) {
		LOGGER.info("Batch started!");
		var cryptoCurrenciesIndexer = DaggerCryptoCurrenciesIndexer.create();
		try {
			LOGGER.info("Creating index = {}", cryptoCurrenciesIndexer.getIndexName());
			cryptoCurrenciesIndexer.indexCreator().createIndex(cryptoCurrenciesIndexer.getIndexName());
			LOGGER.info("Index {} created, starting indexing!", cryptoCurrenciesIndexer.getIndexName());
			cryptoCurrenciesIndexer.cryptoIndexer().indexAllCryptoCurrencies();
			LOGGER.info("Batch completed!");
			System.exit(SUCCESS_STATUS);
		}
		catch (Exception exception) {
			LOGGER.error("ERROR", exception);
			System.exit(ERROR_STATUS);
		}
	}
}
