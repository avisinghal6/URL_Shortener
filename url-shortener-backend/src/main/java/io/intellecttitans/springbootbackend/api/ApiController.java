package io.intellecttitans.springbootbackend.api;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
	@RequestMapping("/api/{longUrl}")
	public String longToShortUrl(@PathVariable String longUrl ) {
		return longUrl;
	}
	
}
