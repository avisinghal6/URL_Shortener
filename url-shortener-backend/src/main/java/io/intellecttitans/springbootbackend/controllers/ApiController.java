package io.intellecttitans.springbootbackend.controllers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.util.List;
import java.util.Date;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.net.URI;

import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.intellecttitans.springbootbackend.configurations.UrlTable;
import io.intellecttitans.springbootbackend.configurations.UserTable;
import io.intellecttitans.springbootbackend.utils.Base62Encoding;
import io.intellecttitans.springbootbackend.utils.CustomOAuth2User;
import io.intellecttitans.springbootbackend.utils.UserDetails;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ApiController {

	@Autowired
	private UrlTable urlTable;
	
	@Autowired
	private UserTable userTable;

	@RequestMapping(value="/api/longurl",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String>longToShortUrl(@RequestParam("longurl") String long_url) {
		Date currentDate = new Date();
		String shortUrl = Base62Encoding.base62Encoding();
		List<String> subFamily = new ArrayList<>();
		subFamily.add("long_url");
		subFamily.add("created");

		List<String> value = new ArrayList<>();
		value.add(long_url);
		value.add(currentDate.toString());
		if(!urlTable.writeRow(value, subFamily, shortUrl)) {
			new ResponseEntity<>("Error writing to URL table", HttpStatus.BAD_REQUEST);
		}
		
		List<String> subFamily2 = new ArrayList<>();
		subFamily2.add("List_of_Urls");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
			UserDetails oauthUser = (UserDetails) auth.getPrincipal();
			List<String> data= userTable.getRow(oauthUser.getEmail());
		
			List<String> finalData= new ArrayList<>();
			finalData.add(data.get(0)+","+shortUrl);
			data.set(0, data.get(0)+","+shortUrl);
			if(!userTable.writeRow(finalData, subFamily2, oauthUser.getEmail())) {
				new ResponseEntity<>("Error writing to user table", HttpStatus.BAD_REQUEST);
			}
		}
		
		return new ResponseEntity<>(shortUrl, HttpStatus.OK);
		
	}

	
	@RequestMapping("/api/shorturl/{shortUrl}")
	public ResponseEntity<Void> shortToLongUrl(@PathVariable String shortUrl) {
		List<String> longUrl = urlTable.getRow(shortUrl);
		if (longUrl.get(1).substring(0,4).equals("http")) {
			return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl.get(1))).build();
		}
		else {
			return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://" + longUrl.get(1))).build();
		}
		
	}
	
	@RequestMapping(value="/api/batch",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<List<String>> batchConversion(@RequestParam("urlBatch") List<String>  batch) throws Exception{
		System.out.println(batch);
		List<String> subFamily = new ArrayList<>();
		List<String> output = new ArrayList<>();
		subFamily.add("long_url");
		subFamily.add("created");
		for (int i = 0; i < batch.size(); i++) {
//            System.out.println(batch.get(i));
			Date currentDate = new Date();
			String shortUrl = Base62Encoding.base62Encoding();
			List<String> value = new ArrayList<>();
			value.add(batch.get(i));
			value.add(currentDate.toString());
			if(!urlTable.writeRow(value, subFamily, shortUrl)) {
				new ResponseEntity<>("Error writing "+ batch.get(i)+" to URL table", HttpStatus.BAD_REQUEST);
			}
			
			output.add(shortUrl);
			
			
        }
		
		return new ResponseEntity<>(output,HttpStatus.OK);
	}
	
	@RequestMapping(value="/api/barcode",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> shortToLongUrlBarCode(@RequestParam("longurl") String longUrl) throws Exception{
		try {
	    	QRCodeWriter barcodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = 
		      barcodeWriter.encode(longUrl, BarcodeFormat.QR_CODE, 200, 200);
		    
		    BufferedImage image= MatrixToImageWriter.toBufferedImage(bitMatrix);

		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ImageIO.write(image, "PNG", baos);
		    byte[] bytes = baos.toByteArray();
		    String bytesBase64 = Base64.encodeBase64String(bytes);

	        return new ResponseEntity<>(bytesBase64, HttpStatus.OK);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return new ResponseEntity<>("Error generating barcode", HttpStatus.FORBIDDEN);   
	    }
	}
	

}
