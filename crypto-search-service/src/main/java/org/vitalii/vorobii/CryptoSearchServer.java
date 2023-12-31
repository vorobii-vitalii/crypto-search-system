package org.vitalii.vorobii;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.components.DaggerCryptoSearch;

public class CryptoSearchServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoSearchServer.class);

	public static void main(String[] args) throws IOException, InterruptedException {
		var cryptoSearch = DaggerCryptoSearch.create();
		var server = cryptoSearch.server();
		LOGGER.info("Starting server!");
		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
		server.start().awaitTermination();
	}
}
