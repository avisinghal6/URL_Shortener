package io.intellecttitans.springbootbackend.controllers;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.intellecttitans.springbootbackend.configurations.UrlTable;
import io.intellecttitans.springbootbackend.configurations.UserTable;
import io.intellecttitans.springbootbackend.utils.Base62Encoding;
import io.intellecttitans.springbootbackend.utils.CustomOAuth2User;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@RestController
public class ApiController {

	@Autowired
	private UrlTable urlTable;
	
	@Autowired
	private UserTable userTable;

	@RequestMapping(value="/api/longurl/{longUrl}",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> longToShortUrl(@RequestParam("longurl") String long_url) {
		Date currentDate = new Date();
		String shortUrl = Base62Encoding.base62Encoding();
		List<String> subFamily = new ArrayList<>();
		subFamily.add("long_url");
		subFamily.add("created");

		List<String> value = new ArrayList<>();
		value.add(long_url);
		value.add(currentDate.toString());
		urlTable.writeRow(value, subFamily, shortUrl);
		
		List<String> subFamily2 = new ArrayList<>();
		subFamily2.add("List_of_Urls");
		
		List<String> data= userTable.getRow("as278@rice.edu");
		System.out.println(data.get(0)+" "+data.get(1)+" "+data.get(2));
		List<String> finalData= new ArrayList<>();
		
		data.set(0, data.get(0)+","+shortUrl);
		finalData.add(data.get(0));
		userTable.writeRow(finalData, subFamily2, "as278@rice.edu");
		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
//			CustomOAuth2User oauthUser = (CustomOAuth2User) auth.getPrincipal();
////			System.out.println(oauthUser.getName()+" "+ oauthUser.getEmail());
//			List<String> data= userTable.getRow(oauthUser.getEmail());
////			System.out.println(data.get(0)+" "+data.get(1)+" "+data.get(2));
//			data.set(0, data.get(0)+","+shortUrl);
//			userTable.writeRow(data, subFamily2, oauthUser.getEmail());
//		}
		
		System.out.println(long_url);
		return new ResponseEntity<>(shortUrl, HttpStatus.OK);
	}
//	
//	@RequestMapping("/api/longurl/{long_url}")
//	public ResponseEntity<String> longToShortUrl(@PathVariable String long_url) {
//		
//		Date currentDate = new Date();
//		String shortUrl = Base62Encoding.base62Encoding();
//		List<String> subFamily = new ArrayList<>();
//		subFamily.add("long_url");
//		subFamily.add("created");
//
//		List<String> value = new ArrayList<>();
//		value.add(long_url);
//		value.add(currentDate.toString());
//		bigTableObj.writeRow(value, subFamily, shortUrl);
//		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
//			CustomOAuth2User oauthUser = (CustomOAuth2User) auth.getPrincipal();
//			System.out.println(oauthUser.getName()+" "+ oauthUser.getEmail());
//		}
//		System.out.println(long_url);
//		return new ResponseEntity<>(shortUrl, HttpStatus.OK);
//	}

	@RequestMapping("/api/shorturl/{shortUrl}")
	public ResponseEntity<String> shortToLongUrl(@PathVariable String shortUrl) {
		List<String> longUrl = urlTable.getRow(shortUrl);
		if(longUrl!=null)
			return new ResponseEntity<>(longUrl.get(1), HttpStatus.OK);
		else
			return new ResponseEntity<>("Error getting the longUrl", HttpStatus.FORBIDDEN);
	}

	@RequestMapping("/api/barcode/{longUrl}")
	public String shortToLongUrlBarCode(@PathVariable String longUrl) throws Exception  {
		// TODO: create the short URL and bar code, return the bar code, save in
		// database. The return type will be changed.
		

//	    barcodeGenerator.generateBarcode(canvas, "avisinghal.com");
	    
	    
	    try {
	    	QRCodeWriter barcodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = 
		      barcodeWriter.encode(longUrl, BarcodeFormat.QR_CODE, 200, 200);
		    
		    BufferedImage image= MatrixToImageWriter.toBufferedImage(bitMatrix);
		    
	        File outputFile = new File("barcode.png"); // Replace with the desired file path
	        ImageIO.write(image, "PNG", outputFile);
	        System.out.println("Barcode image saved to " + outputFile.getAbsolutePath());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		return "QR generated successfully";
	}

}
