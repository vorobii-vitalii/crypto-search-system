package org.vitalii.vorobii.modules;

import java.io.IOException;

import javax.inject.Named;

import org.vitalii.vorobii.constants.ServerConstants;
import org.vitalii.vorobii.grpc.CryptoSearchServiceImpl;
import org.vitalii.vorobii.service.CryptoSearchService;

import dagger.Module;
import dagger.Provides;
import io.grpc.Server;
import io.grpc.ServerBuilder;

@Module
public class ServerModule {

	@Provides
	public Server server(@Named(ServerConstants.PORT) Integer port, CryptoSearchService cryptoSearchService) {
		return ServerBuilder.forPort(port)
				.addService(new CryptoSearchServiceImpl(cryptoSearchService))
				.build();
	}

	@Named(ServerConstants.PORT)
	@Provides
	public Integer getPort() {
		return Integer.parseInt(System.getenv(ServerConstants.PORT));
	}

}
