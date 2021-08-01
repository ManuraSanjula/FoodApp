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
    private  Path fileStorageLocation;
    
//    @Value("${user-file.upload-dir}")
//    private String userImagePath;
//
//    @Autowired
//    public FileStorageService() {
//        this.fileStorageLocation = Paths.get(userImagePath)
//                .toAbsolutePath().normalize();
//        try {
//            Files.createDirectories(this.fileStorageLocation);
//        } catch (Exception ex) {
//        	
//        }
//    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(fileName.contains("..")) {
                return null;
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
        	return null;
        }
    }
    
    public Flux<String> uploadFile(Path path, Flux<DataBuffer> bufferFlux,String fileName) throws IOException {
        Path opPath = fileStorageLocation.resolve(path);
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(opPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return DataBufferUtils.write(bufferFlux, channel)
                            .map(b -> fileName);
    }


    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
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

