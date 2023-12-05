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
			System.out.println(data.get(0)+" "+data.get(1)+" "+data.get(2));
			List<String> finalData= new ArrayList<>();
			finalData.add(data.get(0)+","+shortUrl);
			data.set(0, data.get(0)+","+shortUrl);
			if(!userTable.writeRow(finalData, subFamily2, oauthUser.getEmail())) {
				new ResponseEntity<>("Error writing to user table", HttpStatus.BAD_REQUEST);
			}
		}
		
		return new ResponseEntity<>("qwklnk.com/"+shortUrl, HttpStatus.OK);
		
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
	
	@RequestMapping(value="/api/shorturl",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> shortToLongUrl(@RequestParam("shorturl") String shortUrl){

		List<String> longUrl = urlTable.getRow(shortUrl.substring(13, 21));
		if(longUrl!=null) {			
			return new ResponseEntity<>(longUrl.get(1).substring(12, longUrl.get(1).length() - 2), HttpStatus.OK);
		}
		else
			return new ResponseEntity<>("Error getting the longUrl", HttpStatus.FORBIDDEN);
		
	}
	
	
//	@RequestMapping("/api/shorturl/{shortUrl}")
//	public ResponseEntity<String> shortToLongUrl(@PathVariable String shortUrl) {
//		List<String> longUrl = urlTable.getRow(shortUrl);
//		if(longUrl!=null)
//			return new ResponseEntity<>(longUrl.get(1), HttpStatus.OK);
//		else
//			return new ResponseEntity<>("Error getting the longUrl", HttpStatus.FORBIDDEN);
//	}
	
	@RequestMapping(value="/api/barcode",method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> shortToLongUrlBarCode(@RequestParam("longurl") String longUrl) throws Exception{
		System.out.println(longUrl);
		try {
	    	QRCodeWriter barcodeWriter = new QRCodeWriter();
		    BitMatrix bitMatrix = 
		      barcodeWriter.encode(longUrl.substring(12, longUrl.length() - 2), BarcodeFormat.QR_CODE, 200, 200);
		    
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
//	@RequestMapping("/api/barcode/{longUrl}")
//	public String shortToLongUrlBarCode(@PathVariable String longUrl) throws Exception  {
//		System.out.println("in QR code");
//		// TODO: create the short URL and bar code, return the bar code, save in
//		// database. The return type will be changed.
//		
//
////	    barcodeGenerator.generateBarcode(canvas, "avisinghal.com");
//	    
//	    
//	    try {
//	    	QRCodeWriter barcodeWriter = new QRCodeWriter();
//		    BitMatrix bitMatrix = 
//		      barcodeWriter.encode(longUrl, BarcodeFormat.QR_CODE, 200, 200);
//		    
//		    BufferedImage image= MatrixToImageWriter.toBufferedImage(bitMatrix);
//		    System.out.println("..................");
//		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		    ImageIO.write(image, "PNG", baos);
//		    byte[] bytes = baos.toByteArray();
//		    String bytesBase64 = Base64.encodeBase64String(bytes);
//		    System.out.println(bytesBase64);
//		    
//		    System.out.println("..................");
//	        File outputFile = new File("barcode.png"); // Replace with the desired file path
//	        ImageIO.write(image, "PNG", outputFile);
//	        System.out.println("Barcode image saved to " + outputFile.getAbsolutePath());
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//		
//		return "yaas queen";
//	}

}
