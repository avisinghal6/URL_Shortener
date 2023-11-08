package io.intellecttitans.springbootbackend.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import io.intellecttitans.springbootbackend.BigTable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.intellecttitans.springbootbackend.Base62Encoding;

@RestController
public class ApiController {

	@Autowired
	private BigTable bigTableObj;

	public ApiController(BigTable bigTable) {
		bigTableObj = bigTable;
	}

	@RequestMapping("/api/longurl/{longUrl}")
	public ResponseEntity<String> longToShortUrl(@PathVariable String longUrl) {
		Date currentDate = new Date();
		String shortUrl = Base62Encoding.base62Encoding();
		List<String> subFamily = new ArrayList<>();
		subFamily.add("long_url");
		subFamily.add("created");

		List<String> value = new ArrayList<>();
		value.add(longUrl);
		value.add(currentDate.toString());
		bigTableObj.writeRow(value, subFamily, shortUrl);
		return new ResponseEntity<>(shortUrl, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping("/api/shorturl/{shortUrl}")
	public ResponseEntity<String> shortToLongUrl(@PathVariable String shortUrl) {
		List<String> longUrl = bigTableObj.getRow(shortUrl);
		return new ResponseEntity<>(longUrl.get(1), HttpStatus.BAD_REQUEST);
	}

	@RequestMapping("/api/barcode/{longUrl}")
	public String shortToLongUrlBarCode(@PathVariable String longUrl) {
		// TODO: create the short URL and bar code, return the bar code, save in
		// database. The return type will be changed.

		return "";
	}

}
