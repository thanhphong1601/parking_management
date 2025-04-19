/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author Admin
 */
public class DownloadImageAsMultipartFile {

    public MultipartFile downloadImageAsMultipartFile(String imageUrl) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(imageUrl, byte[].class);

        if (response.getBody() != null) {
            return new MockMultipartFile("file", "image.jpg", "image/jpeg", response.getBody());
        }
        return null;
    }
}
