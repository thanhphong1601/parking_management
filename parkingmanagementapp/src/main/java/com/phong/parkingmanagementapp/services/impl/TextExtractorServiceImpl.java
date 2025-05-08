/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.services.*;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class TextExtractorServiceImpl implements TextExtractorService {
    @Autowired
    private CloudinaryService cloudinary;
    Dotenv env = Dotenv.load();

    @Override
    public Map<String, ?> extractTextFromImg(MultipartFile file1) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        String imageUrl = this.cloudinary.uploadImage(file1);
        try {
            HttpResponse<String> response = Unirest.post(env.get("OCR_API_URL"))
                    .field("apikey", env.get("OCR_API_KEY"))
                    .field("url",imageUrl)
                    .asString();

            JSONObject jsonResponse = new JSONObject(response.getBody());

            if (jsonResponse.has("ParsedResults")) {
                JSONArray resultsArray = jsonResponse.getJSONArray("ParsedResults");

                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    String parsedText = firstResult.getString("ParsedText");

                    resultMap.put("text", parsedText);
                } else {
                    resultMap.put("error", "No results in ParsedResults.");
                }
            } else {
                resultMap.put("error", "ParsedResults not found.");
            }
        } catch (JSONException e) {
            resultMap.put("error", "Error extracting text: " + e.getMessage());
        }

        return resultMap;
    }

}
