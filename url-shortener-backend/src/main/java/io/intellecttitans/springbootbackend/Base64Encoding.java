package io.intellecttitans.springbootbackend;
import java.util.UUID;

public class Base64Encoding {
	
	public static String base64Encoding() {
		
		UUID uuid = UUID.randomUUID();
        System.out.println("Random UUID: " + uuid);
        return "";
		
	}
	
	public static void main(String... args) {
		Base64Encoding.base64Encoding();
	}
}
