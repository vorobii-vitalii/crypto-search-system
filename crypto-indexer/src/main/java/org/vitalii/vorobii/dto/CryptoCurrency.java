package org.vitalii.vorobii.dto;

public record CryptoCurrency(String marketName, String symbol, String description) {
	public String getId() {
		return marketName + "/" + symbol;
	}
}
