/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services.impl;

import com.phong.parkingmanagementapp.exceptions.VehicleNotFoundException;
import com.phong.parkingmanagementapp.models.EntryHistory;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.repositories.EntryHistoryRepository;
import com.phong.parkingmanagementapp.repositories.PositionRepository;
import com.phong.parkingmanagementapp.repositories.UserRepository;
import com.phong.parkingmanagementapp.repositories.VehicleRepository;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.VehicleService;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private PositionRepository posRepo;
    
    private String vehicleIdString = "21";

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
            //System.out.println(timestampStr);
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

    @Override
    public Page<EntryHistory> findAllByName(Pageable pageable, String name) {
        return this.entryRepo.findAllByName(pageable, name);
    }

    @Override
    public Map<String, String> countEntriesByDay(LocalDate date) {
        Date startOfDay = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Tính số lượt vào
        Long countIn = this.entryRepo.countByTimeInBetween(startOfDay, endOfDay);

        // Tính số lượt ra
        Long countOut = this.entryRepo.countByTimeOutBetween(startOfDay, endOfDay);

        return new HashMap<>() {
            {
                put("inCount", countIn.toString());
                put("outCount", countOut.toString());
            }
        };
    }

    @Override
    public Map<String, String> processPlateRecognize(Map<String, String> params, MultipartFile file) throws IOException, ParseException {
        Map<String, Object> result = new HashMap<>();

        //from result got from recognizing -> get vehicle
        Map<String, String> resultsFromRecognizingPlate = new HashMap<>();

        if (file != null) {
            resultsFromRecognizingPlate = recognizePlate(file);

            if (resultsFromRecognizingPlate.get("plate") == null) {
                result.put("Status: ", "Cannot get plate license from image!");
            }

            result.put("plate", resultsFromRecognizingPlate.get("plate"));
            result.put("timeIn", resultsFromRecognizingPlate.get("timestamp"));

            String plateLicense = resultsFromRecognizingPlate.get("plate");

            if (existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(plateLicense)) {
                result.put("Status: ", "Vehicle is already appeared");
            }

            Vehicle currentVehicle = this.vehicleService.findByLicensePlateNumberIgnoreCase(plateLicense)
                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with plate: " + plateLicense));

            result.put("vehicle", currentVehicle);

            //from vehicle -> get user
            User user = currentVehicle.getUser();
            if (user == null) {
                result.put("Status: ", "User not found! Please check your infomation");
            }

            result.put("owner", user);

            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                result.put("Status", "No valid ticket found!");
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);
        } else {
            Vehicle currentVehicle = this.vehicleService.getVehicleById(Integer.parseInt(params.get("vehicleId")));
            if (existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(currentVehicle.getPlateLicense())) {
                result.put("Status", "Vehicle is already appeared");
            }

            result.put("plate", currentVehicle.getPlateLicense());
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            String timestamp = now.format(formatter);
            result.put("timeIn", convertToDate(timestamp).toString());
            result.put("vehicle", currentVehicle);

            User user = currentVehicle.getUser();
            if (user == null) {
                result.put("Status: ", "User not found! Please check your infomation");
            }

            result.put("owner", user);

            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                result.put("Status: ", "No favid ticket found!");
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);
        }
        return null;
    }

    @Override
    public Map<String, Object> processTicketInput(Map<String, String> params) {
        Map<String, Object> userInfoResult = new HashMap<>();
        String ticketIdParam = params.get("ticketId");
        int ticketId = 0;
        if (!ticketIdParam.isEmpty()) {
            ticketId = Integer.parseInt(ticketIdParam);
        }

        Date currentDate = new Date();

        Ticket t = this.ticketService.checkTicketDate(ticketId, currentDate);

        if (t != null) {
            userInfoResult.put("user", t.getUserOwned());

            userInfoResult.put("vehicle", t.getVehicle().getName());

            userInfoResult.put("ticketId", t.getId());

            userInfoResult.put("ticketType", t.getTicketType()!= null ? t.getTicketType().getType() : "");

            //more info if needed here
            //->>>
        }

        return userInfoResult;
    }

    @Override
    public Map<String, ?> processComparingImg(MultipartFile file1, MultipartFile file2) throws IOException {
        Dotenv env = Dotenv.load();
        Map<String, Object> resultMap = new HashMap<>();

        try {
            HttpResponse<String> response = Unirest.post(env.get("COMPARING_REQUEST_URL"))
                    .field("api_key", env.get("COMPARING_API_KEY"))
                    .field("api_secret", env.get("COMPARING_API_SECRET"))
                    .field("image_file1", file1.getInputStream(), file1.getOriginalFilename())
                    .field("image_file2", file2.getInputStream(), file2.getOriginalFilename())
                    .asString();

            JSONObject jsonResponse = new JSONObject(response.getBody());

            if (jsonResponse.has("confidence")) {
                double confidence = jsonResponse.getDouble("confidence");
                resultMap.put("confidence", confidence);

                //compare the point got, >= 80.0 if same person
                boolean isSamePerson = confidence >= 80.0;
                resultMap.put("isSamePerson", isSamePerson);
            } else {
                resultMap.put("error", "No confidence value found");
            }
        } catch (IOException | JSONException e) {
            resultMap.put("error", "Error comparing faces: " + e.getMessage());
        }
        return resultMap;
    }
    
    @Override
    public void recordVehicleIn(Map<String, String> attributes){
        //attributes are timeIn, userId, creator_id, vehicleId, ticketId
        EntryHistory entry = new EntryHistory();
        entry.setPlateImgIn(attributes.get("inImgUrl"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(attributes.get("timeIn"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        Date date = Date.from(zonedDateTime.toInstant());

        entry.setTimeIn(date);
        
        //uploading to Cloudinary to get url
        entry.setPersonImgIn(attributes.get("personImgIn"));
        
        
        Ticket ticket = this.ticketService.findByTicketId(attributes.get("ticketId"));
        entry.setTicket(ticket);

        User owner = this.userRepo.getUserById(Integer.parseInt(attributes.get("userId")));
        entry.setOwner(owner);
        entry.setCreator(this.userRepo.getUserById(Integer.parseInt(attributes.get("creatorId"))));
        Vehicle vehicle = this.vehicleRepo.getVehicleById(Integer.parseInt(attributes.getOrDefault("vehicleId", vehicleIdString)));
        entry.setVehicle(vehicle);
        
        //saving the plate number to ticket's plate number field -> on the controller
        //call recognizePlate() func on controller to recognize the plate -> put the result to attributes
        this.ticketService.updateLicenseField(ticket.getId(),
                attributes.get("plateNumber"));
        
        

        this.entryRepo.save(entry);
    }

    @Override
    public void recordVehicleOut(Map<String, String> attributes) {
        //check ticket valid (date usage, paid, same id) on controller
        //compare 2 pics if same (plate number & person picture)
        
        Ticket currentTicket = this.ticketService.findByTicketId(attributes.get("ticketId"));
        EntryHistory currentEntryRecord = this.entryRepo.getEntryHistoryByTicketId(currentTicket.getId());
        
        currentEntryRecord.setPlateImgOut(attributes.get("outImgUrl"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(attributes.get("timeOut"), formatter.withZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        Date date = Date.from(zonedDateTime.toInstant());

        currentEntryRecord.setTimeOut(date);
        
        //uploading to Cloudinary to get url
        currentEntryRecord.setPersonImgOut(attributes.get("personImgOut"));
        
//        Position currentPosition = currentTicket.getPosition();
//        currentPosition.setPlateImgUrl(null);
//        this.posRepo.save(currentPosition);
        
        this.entryRepo.save(currentEntryRecord);
    }

    @Override
    public Boolean processComparePlates(MultipartFile file1, MultipartFile file2) throws IOException {
        String plateResult1 = "**";
        String plateResult2 = "@@";
        
        plateResult1 = this.recognizePlate(file1).get("plate");
        plateResult2 = this.recognizePlate(file2).get("plate");
        
        
        if (plateResult1.equalsIgnoreCase(plateResult2))
            return true;
        
        return false;
    }

    @Override
    public EntryHistory getEntryHistoryByTicketId(int ticketId) {
        return this.entryRepo.getEntryHistoryByTicketId(ticketId);
    }

    @Override
    public Page<EntryHistory> getEntryHistoryListByLicensePlateNumberPageable(String licensePlateNumber, Pageable pageable) {
        return this.entryRepo.getEntryHistoryListByLicensePlateNumberPageable(licensePlateNumber, pageable);
    }
}
