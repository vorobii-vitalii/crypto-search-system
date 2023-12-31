package org.vitalii.vorobii.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class TestJacksonBodyHandler {

	JacksonBodyHandler<Person> bodyHandler = new JacksonBodyHandler<>(new TypeReference<>() {
	});

	HttpResponse.ResponseInfo responseInfo = mock(HttpResponse.ResponseInfo.class);

	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void apply() throws JsonProcessingException, ExecutionException, InterruptedException {
		var bodySubscriber = bodyHandler.apply(responseInfo);
		var json = objectMapper.writeValueAsString(new Person("George", 24)).getBytes(StandardCharsets.UTF_8);
		bodySubscriber.onNext(List.of(ByteBuffer.wrap(json)));
		bodySubscriber.onComplete();
		assertThat(bodySubscriber.getBody().toCompletableFuture().get().get()).isEqualTo(new Person("George", 24));
	}

	record Person(String name, int age) {
	}

}