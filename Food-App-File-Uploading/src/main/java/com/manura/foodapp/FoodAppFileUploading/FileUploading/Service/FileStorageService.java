package com.manura.foodapp.FoodAppFileUploading.FileUploading.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Flux;

@Service
public class FileStorageService {

	@Value("${user-file.upload-dir}")
    private  Path userFileStorageLocation;
	
	@Value("${food-file.upload-dir}")
    private  Path foodFileStorageLocation;
    
    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(fileName.contains("..")) {
                return null;
            }
            Path targetLocation = this.userFileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
        	return null;
        }
    }
    
    public Flux<String> uploadFile(Path path, Flux<DataBuffer> bufferFlux,String fileName,String type) throws IOException {
        if(type.equals("food")) {
        	Path opPath = foodFileStorageLocation.resolve(path);
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(opPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return DataBufferUtils.write(bufferFlux, channel)
                                .map(b -> fileName);
        }else {
        	Path opPath = userFileStorageLocation.resolve(path);
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(opPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            return DataBufferUtils.write(bufferFlux, channel)
                                .map(b -> fileName);
        }
    }


    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.userFileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
            	return null;
            }
        } catch (MalformedURLException ex) {
        	return null;
        }

    }
}

