package org.vitalii.vorobii.grpc;

import java.nio.file.LinkOption;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vitalii.vorobii.service.CryptoSearchService;

import com.example.crypto_search.Crypto;
import com.example.crypto_search.CryptoSearchRequest;
import com.example.crypto_search.CryptoSearchServiceGrpc;

import co.elastic.apm.api.CaptureTransaction;
import io.grpc.stub.StreamObserver;
import reactor.core.CoreSubscriber;

public class CryptoSearchServiceImpl extends CryptoSearchServiceGrpc.CryptoSearchServiceImplBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoSearchService.class);

	private final CryptoSearchService cryptoSearchService;

	public CryptoSearchServiceImpl(CryptoSearchService cryptoSearchService) {
		this.cryptoSearchService = cryptoSearchService;
	}

	@CaptureTransaction
	@Override
	public void search(CryptoSearchRequest request, StreamObserver<Crypto> responseObserver) {
		LOGGER.info("Received search request {}", request);
		cryptoSearchService.findCryptoCurrencies(request)
				.subscribe(new CoreSubscriber<>() {
					private Subscription s;

					@Override
					public void onSubscribe(Subscription s) {
						s.request(1);
						this.s = s;
					}

					@Override
					public void onNext(Crypto crypto) {
						LOGGER.info("Found new crypto {}", crypto);
						responseObserver.onNext(crypto);
						s.request(1);
					}

					@Override
					public void onError(Throwable t) {
						LOGGER.warn("Error", t);
						responseObserver.onError(t);
					}

					@Override
					public void onComplete() {
						LOGGER.info("Completed request...");
						responseObserver.onCompleted();
					}
				});
	}
}
