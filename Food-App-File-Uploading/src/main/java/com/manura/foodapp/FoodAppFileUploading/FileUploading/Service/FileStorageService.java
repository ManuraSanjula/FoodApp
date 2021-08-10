package com.manura.foodapp.FoodAppFileUploading.FileUploading.Service;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class FileStorageService {

	@Value("${user-file.upload-dir}")
	private Path userFileStorageLocation;

	@Value("${food-file.upload-dir}")
	private Path foodFileStorageLocation;

	public String storeFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (fileName.contains("..")) {
				return null;
			}
			Path targetLocation = this.userFileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (IOException ex) {
			return null;
		}
	}
	
	private  void addTextWatermark(File watermark, String type, File source, File destination) {
       Runnable imageThread = ()->{
    	   try {
    		   BufferedImage image = ImageIO.read(source);
    	        BufferedImage overlay = resize(ImageIO.read(watermark), 150, 150);

    	        // determine image type and handle correct transparency
    	        int imageType = "jpeg".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
    	        BufferedImage watermarked = new BufferedImage(image.getWidth(), image.getHeight(), imageType);

    	        // initializes necessary graphic properties
    	        Graphics2D w = (Graphics2D) watermarked.getGraphics();
    	        w.drawImage(image, 0, 0, null);
    	        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    	        w.setComposite(alphaChannel);

    	        // calculates the coordinate where the String is painted
    	        int centerX = image.getWidth() / 2;
    	        int centerY = image.getHeight() / 2;

    	        // add text watermark to the image
    	        w.drawImage(overlay, centerX, centerY, null);
    	        ImageIO.write(watermarked, type, destination);
    	        w.dispose();
    	   }catch (Exception e) {
			// TODO: handle exception
		}
       };
       new Thread(imageThread).start();
    }

    private  BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
    
    private void addTextWatermark(String text, String type, File source, File destination) {
        Runnable imageThread = ()->{
        	 try {
        		 BufferedImage image = ImageIO.read(source);

                 // determine image type and handle correct transparency
                 int imageType = "png".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
                 BufferedImage watermarked = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
                 
                 int fontSize = (((image.getWidth() + (image.getHeight() / 2)) / 10));
                 // initializes necessary graphic properties
                 Graphics2D w = (Graphics2D) watermarked.getGraphics();
                 w.drawImage(image, 0, 0, null);
                 AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
                 w.setComposite(alphaChannel);
                 w.setColor(Color.GRAY);
                 w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
                 FontMetrics fontMetrics = w.getFontMetrics();
                 Rectangle2D rect = fontMetrics.getStringBounds(text, w);

                 // calculate center of the image
                 int centerX = (image.getWidth() - (int) rect.getWidth()) / 2;
                 int centerY = image.getHeight() / 2;

                 // add text overlay to the image
                 w.drawString(text, centerX, centerY);
                 ImageIO.write(watermarked, type, destination);
                 w.dispose();
        	 }catch (Exception e) {
				// TODO: handle exception
			}
        };
        new Thread(imageThread).start();
    }

   
	public Flux<String> uploadFileUser(Path path, Flux<DataBuffer> bufferFlux, String fileName) {
        try {
        	Path opPath = userFileStorageLocation.resolve(path);
    		AsynchronousFileChannel channel = AsynchronousFileChannel.open(opPath, StandardOpenOption.CREATE,
    				StandardOpenOption.WRITE);
    		return DataBufferUtils.write(bufferFlux, channel).map(b -> fileName).publishOn(Schedulers.boundedElastic())
    				.subscribeOn(Schedulers.boundedElastic());

        }catch (Exception e) {
        	return Flux.empty();
		}
	}

	public Flux<String> uploadFileFood(Path path, Flux<DataBuffer> bufferFlux, String fileName){
		try {
			Path opPath = foodFileStorageLocation.resolve(path);

			AsynchronousFileChannel channel = AsynchronousFileChannel.open(opPath, StandardOpenOption.CREATE,
					StandardOpenOption.WRITE);
			return DataBufferUtils.write(bufferFlux, channel).map(b -> fileName)
					.doOnComplete(()->{
						File image = new File(opPath.toAbsolutePath().toString());
						addTextWatermark("Food-App", "jpeg", image, image);
						//addTextWatermark("Food-App", image, image);
					}).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		}catch (Exception e) {
			return Flux.empty();
		}
	}

	public Resource loadFileAsResourceUser(String fileName) {
		try {
			Path filePath = this.userFileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {

				return resource;
			} else {
				return null;
			}
		} catch (MalformedURLException ex) {
			return null;
		}

	}

	public Resource loadFileAsResourceFood(String fileName) {
		try {
			Path filePath = this.foodFileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				return null;
			}
		} catch (MalformedURLException ex) {
			return null;
		}

	}
}
