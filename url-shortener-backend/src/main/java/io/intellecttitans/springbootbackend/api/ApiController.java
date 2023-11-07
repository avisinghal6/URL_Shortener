package io.intellecttitans.springbootbackend.api;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.intellecttitans.springbootbackend.Base62Encoding;

@RestController
public class ApiController {
	@RequestMapping("/api/{longUrl}")
	public String longToShortUrl(@PathVariable String longUrl ) {
		//TODO: insert the long,short URL in the big table.
		String shortUrl=Base62Encoding.base62Encoding();
		return shortUrl;
	}
	
	@RequestMapping("/api/{shortUrl}")
	public String shortToLongUrl(@PathVariable String shortUrl ) {
		//TODO: query the data base to retrieve the long url for the corresponding short url.
		
		return "";
	}
	
	@RequestMapping("/api/barcode/{longUrl}")
	public String shortToLongUrlBarCode(@PathVariable String longUrl ) {
		//TODO: create the short URL and bar code, return the bar code, save in database. The return type will be changed.
		
		return "";
	}
	
}