/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.exceptions.VehicleNotFoundException;
import com.phong.parkingmanagementapp.models.EntryHistory;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.repositories.EntryHistoryRepository;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.repositories.VehicleRepository;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class EntryHistoryServiceImpl implements EntryHistoryService {

    @Autowired
    private EntryHistoryRepository entryRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private VehicleRepository vehicleRepo;

    @Override
    public Map<String, String> recognizePlate(MultipartFile file) throws IOException {
        Dotenv env = Dotenv.load();
        Map<String, String> resultMap = new HashMap<>();

//        File image = convertMultipartFileToFile(file);
        try {
            HttpResponse<String> response = Unirest.post("https://api.platerecognizer.com/v1/plate-reader/")
                    .header("Authorization", "Token " + env.get("RECOGNIZER_API_KEY"))
                    .field("upload", file.getInputStream(), file.getOriginalFilename())
                    .asString();

//            System.out.println("Recognize:");
//            System.out.println(response.getBody().toString());
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray resultsArray = jsonResponse.getJSONArray("results");

            if (resultsArray.length() > 0) {
                JSONObject result = resultsArray.getJSONObject(0);
                String plate = result.getString("plate");
                resultMap.put("plate", plate);
            }

            // Lấy timestamp và chuyển đổi sang kiểu Date
            String timestampStr = jsonResponse.getString("timestamp");
            System.out.println(timestampStr);
            resultMap.put("timestamp", convertToDate(timestampStr).toString());

        } catch (Exception e) {
            System.out.println(e);
        }

        return resultMap;

    }

    @Override
    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

    @Override
    public Date convertToDate(String timestampStr) throws ParseException {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX");

        Instant instant = Instant.parse(timestampStr);

        Date date = Date.from(instant);

        //for formatting the date value to string
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        return date;
    }

    @Override
    public void saveHistoryIn(Map<String, String> attributes) {
        //attribute need timeIn, userId, creator_username, vehicleId
        EntryHistory entry = new EntryHistory();
        entry.setPlateImgIn(attributes.get("inImgUrl"));

//        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:s");
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
//        LocalDateTime localDateTime = LocalDateTime.parse(attributes.get("timeIn"), formatter);
//        Date date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(attributes.get("timeIn"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        Date date = Date.from(zonedDateTime.toInstant());

        entry.setTimeIn(date);

        User owner = this.userRepo.getUserById(Integer.parseInt(attributes.get("userId")));
        entry.setOwner(owner);
        entry.setCreator(this.userRepo.getUserById(Integer.parseInt(attributes.get("creatorId"))));
        Vehicle vehicle = this.vehicleRepo.getVehicleById(Integer.parseInt(attributes.get("vehicleId")));
        entry.setVehicle(vehicle);

        this.entryRepo.save(entry);
    }

    @Override
    public void saveHistoryOut(Map<String, String> attributes) {
        //attribute needs id (optional), licensePlateNum, outImgUrl, timeOut
        if (attributes.get("id") == null) {
            Vehicle currentVehicle = this.vehicleRepo
                    .findByLicensePlateNumberIgnoreCase(attributes.get("licensePlateNum"))
                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with plate: " + attributes.get("licensePlateNum")));

            List<EntryHistory> entryHistoryList = this.entryRepo
                    .findEntryHistoriesByLicensePlateNumberAndTimeOutAndPlateImgOut(currentVehicle.getPlateLicense());

            EntryHistory entry = entryHistoryList.get(0);

            entry.setPlateImgOut(attributes.get("outImgUrl"));

//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
//            LocalDateTime localDateTime = LocalDateTime.parse(attributes.get("timeOut"), formatter);
//            Date date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(attributes.get("timeOut"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
            Date date = Date.from(zonedDateTime.toInstant());

            entry.setTimeOut(date);

            this.entryRepo.save(entry);
        } else {
            EntryHistory entry = this.entryRepo.getEntryHistoryById(Integer.parseInt(attributes.get("id")));
            entry.setPlateImgOut(attributes.get("outImgUrl"));

//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
//            LocalDateTime localDateTime = LocalDateTime.parse(attributes.get("timeOut"), formatter);
//            Date date = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(attributes.get("timeOut"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
            Date date = Date.from(zonedDateTime.toInstant());

            entry.setTimeOut(date);

            this.entryRepo.save(entry);
        }
    }

    @Override
    public boolean existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(String licensePlateNumber) {
        return this.entryRepo.existsByLicensePlateNumberAndTimeOutAndPlateImgOutNull(licensePlateNumber);
    }

    @Override
    public List<EntryHistory> findEntryHistoriesByTimeOutAndPlateImgOut(String licensePlate) {
        return this.entryRepo.findEntryHistoriesByTimeOutAndPlateImgOut(licensePlate);
    }

    @Override
    public EntryHistory getEntryHistoryById(int id) {
        return this.entryRepo.getEntryHistoryById(id);
    }

    @Override
    public EntryHistory getEntryHistoryByVehiclePlateLicense(String plateLicense) {
        return this.entryRepo.getEntryHistoryByVehiclePlateLicense(plateLicense);
    }

    @Override
    public long countByDate(Date date) {
        return this.entryRepo.countByDate(date);
    }

    @Override
    public long countByMonth(int month, int year) {
        return this.entryRepo.countByMonth(month, year);
    }

    @Override
    public Double findAverageParkingDuration() {
        return this.entryRepo.findAverageParkingDuration();
    }

}
