package com.moonsystem.gestion_commerciale.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    public String uploadFile(MultipartFile file, String folderName);

}
