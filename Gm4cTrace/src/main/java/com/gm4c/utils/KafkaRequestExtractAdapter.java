package com.gm4c.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;

import io.opentracing.propagation.TextMap;

public class KafkaRequestExtractAdapter implements TextMap {
	private final Map<String, String> headers;

	KafkaRequestExtractAdapter(HttpHeaders header) {
		this.headers = new LinkedHashMap<>();

		Map<String, String> map = (HashMap<String, String>) header.toSingleValueMap();
		map.forEach((k, v) -> {
			headers.put(k, v);
		});
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return headers.entrySet().iterator();
	}

	@Override
	public void put(String key, String value) {
		throw new UnsupportedOperationException();
	}
}