package org.vitalii.vorobii.modules;

import java.net.http.HttpClient;

import javax.inject.Named;

import org.vitalii.vorobii.constants.FinnHubApiConstants;
import org.vitalii.vorobii.service.CryptoCurrenciesService;
import org.vitalii.vorobii.service.impl.FinnHubCryptoCurrenciesService;

import dagger.Module;
import dagger.Provides;

@Module
public class FinnHubModule {

	@Provides
	CryptoCurrenciesService finnHubCryptoCurrenciesService(
			HttpClient httpClient,
			@Named(FinnHubApiConstants.API_KEY) String apiKey,
			@Named(FinnHubApiConstants.PROTOCOL) String protocol,
			@Named(FinnHubApiConstants.HOST_PORT) String hostPort
	) {
		return new FinnHubCryptoCurrenciesService(httpClient, apiKey, protocol, hostPort);
	}

	@Provides
	public HttpClient httpClient() {
		return HttpClient.newHttpClient();
	}

	@Provides
	@Named(FinnHubApiConstants.API_KEY)
	public String getApiKey() {
		return System.getenv(FinnHubApiConstants.API_KEY);
	}


	@Provides
	@Named(FinnHubApiConstants.PROTOCOL)
	public String getProtocol() {
		return System.getenv(FinnHubApiConstants.PROTOCOL);
	}

	@Provides
	@Named(FinnHubApiConstants.HOST_PORT)
	public String getHostPort() {
		return System.getenv(FinnHubApiConstants.HOST_PORT);
	}

}
