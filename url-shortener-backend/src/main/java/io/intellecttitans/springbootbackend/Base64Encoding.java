package io.intellecttitans.springbootbackend;

import java.util.UUID;
import java.util.Random;

public class Base64Encoding {

	public static String base62Encoding() {
		UUID uuid = UUID.randomUUID();

		long MSB = uuid.getMostSignificantBits();
		long LSB = uuid.getLeastSignificantBits();

		String output = base62Encode(MSB) + base62Encode(LSB);
		System.out.println(output);
		return output;

	}

	public static String base62Encode(long value) {
		String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		int base = characters.length();
		StringBuilder sb = new StringBuilder();

		value = Math.abs(value);

		do {
			sb.insert(0, characters.charAt((int) (value % base)));
			value /= base;
		} while (value > 0);

		return selectRandomCharacters(sb, 4);
	}

	public static String selectRandomCharacters(StringBuilder inputString, int n) {
		if (n <= 0) {
			return "";
		}

		Random random = new Random();
		int stringLength = inputString.length();
		StringBuilder selectedChars = new StringBuilder();

		for (int i = 0; i < n; i++) {
			// Using uniform distribution
			int randomIndex = random.nextInt(stringLength);
			char selectedChar = inputString.charAt(randomIndex);
			selectedChars.append(selectedChar);
		}

		return selectedChars.toString();
	}

	public static void main(String... args) {
		Base64Encoding.base62Encoding();
	}
}
