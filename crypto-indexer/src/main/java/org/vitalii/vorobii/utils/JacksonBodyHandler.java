package org.vitalii.vorobii.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonBodyHandler<W> implements HttpResponse.BodyHandler<Supplier<W>> {
	private final TypeReference<W> typeReference;

	public static <W> HttpResponse.BodySubscriber<Supplier<W>> asJSON(TypeReference<W> typeReference) {
		var upstream = HttpResponse.BodySubscribers.ofInputStream();
		return HttpResponse.BodySubscribers.mapping(upstream, stream -> toSupplierOfType(stream, typeReference));
	}

	public static <W> Supplier<W> toSupplierOfType(InputStream inputStream, TypeReference<W> typeReference) {
		return () -> {
			try (var stream = inputStream) {
				// TODO: Make it parameterized
				var objectMapper = new ObjectMapper();
				return objectMapper.readValue(stream, typeReference);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

	public JacksonBodyHandler(TypeReference<W> typeReference) {
		this.typeReference = typeReference;
	}

	@Override
	public HttpResponse.BodySubscriber<Supplier<W>> apply(HttpResponse.ResponseInfo responseInfo) {
		return asJSON(typeReference);
	}
}

