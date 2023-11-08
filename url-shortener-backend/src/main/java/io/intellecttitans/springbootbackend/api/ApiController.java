package io.intellecttitans.springbootbackend.api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import org.springframework.core.io.InputStreamResource;
import java.io.InputStream;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import io.intellecttitans.springbootbackend.BigTable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import io.intellecttitans.springbootbackend.Base62Encoding;

@CrossOrigin(origins = "http://localhost:3001")
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
		return new ResponseEntity<>(shortUrl, HttpStatus.OK);
	}

	@RequestMapping("/api/shorturl/{shortUrl}")
	public ResponseEntity<String> shortToLongUrl(@PathVariable String shortUrl) {
		List<String> longUrl = bigTableObj.getRow(shortUrl);
		if(longUrl!=null)
			return new ResponseEntity<>(longUrl.get(1), HttpStatus.OK);
		else
			return new ResponseEntity<>("Error getting the longUrl", HttpStatus.FORBIDDEN);
	}

	@RequestMapping("/api/barcode/{longUrl}")
	public ResponseEntity<InputStreamResource> shortToLongUrlBarCode(@PathVariable String longUrl) throws Exception  {
		// TODO: create the short URL and bar code, return the bar code, save in
		// database. The return type will be changed.
		

//	    barcodeGenerator.generateBarcode(canvas, "avisinghal.com");
	    
	    
	    try {
	    	QRCodeWriter barcodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = 
		      barcodeWriter.encode(longUrl, BarcodeFormat.QR_CODE, 200, 200);
		    
		    BufferedImage image= MatrixToImageWriter.toBufferedImage(bitMatrix);

	        ByteArrayOutputStream outputFile = new ByteArrayOutputStream();
	        ImageIO.write(image, "png", outputFile);
			InputStream is = new ByteArrayInputStream(outputFile.toByteArray());
			return ResponseEntity
					.ok()
					.contentType(MediaType.IMAGE_PNG)
					.body(new InputStreamResource(is));
	    } catch (IOException e) {
	        e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new InputStreamResource(new ByteArrayInputStream(new byte[0])));
	    }


	}

}
