package org.vitalii.vorobii.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CryptoCurrency(String marketName, String symbol, String description) {
}
