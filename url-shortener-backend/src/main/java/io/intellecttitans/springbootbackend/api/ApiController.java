package io.intellecttitans.springbootbackend.api;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.intellecttitans.springbootbackend.Base62Encoding;

@RestController
public class ApiController {
	@RequestMapping("/api/{longUrl}")
	public String longToShortUrl(@PathVariable String longUrl ) {
		
		String shortUrl=Base62Encoding.base62Encoding();
		return shortUrl;
	}
	
}
