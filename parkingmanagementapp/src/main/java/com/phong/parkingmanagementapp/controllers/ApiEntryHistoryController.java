/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.controllers;

import com.phong.parkingmanagementapp.exceptions.VehicleNotFoundException;
import com.phong.parkingmanagementapp.models.EntryHistory;
import com.phong.parkingmanagementapp.models.Floor;
import com.phong.parkingmanagementapp.models.Position;
import com.phong.parkingmanagementapp.models.Ticket;
import com.phong.parkingmanagementapp.models.User;
import com.phong.parkingmanagementapp.models.Vehicle;
import com.phong.parkingmanagementapp.services.CloudinaryService;
import com.phong.parkingmanagementapp.services.EntryHistoryService;
import com.phong.parkingmanagementapp.services.PositionService;
import com.phong.parkingmanagementapp.services.TextExtractorService;
import com.phong.parkingmanagementapp.services.TicketService;
import com.phong.parkingmanagementapp.services.UserService;
import com.phong.parkingmanagementapp.services.VehicleService;
import com.phong.parkingmanagementapp.utils.DownloadImageAsMultipartFile;
import com.phong.parkingmanagementapp.utils.parseLocalDate;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiEntryHistoryController {

    @Autowired
    private EntryHistoryService entryService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private CloudinaryService cloudService;
    @Autowired
    private PositionService posService;
    @Autowired
    private TextExtractorService textExtractor;

    @Value("${page_size}")
    private int pageSize;

    @PostMapping(path = "/recognize/in/get")
    @CrossOrigin
    public ResponseEntity<?> getRecognizePlateIn(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(required = false) Map<String, String> params) throws IOException, ParseException {

        Map<String, Object> result = new HashMap<>();

        //from result got from recognizing -> get vehicle
        Map<String, String> resultsFromRecognizingPlate = new HashMap<>();

        if (file != null) {
            resultsFromRecognizingPlate = this.entryService.recognizePlate(file);

            if (resultsFromRecognizingPlate.get("plate") == null) {
                return new ResponseEntity<>("Cannot get plate license from image!", HttpStatus.OK);
            }

            result.put("plate", resultsFromRecognizingPlate.get("plate"));
            result.put("timeIn", resultsFromRecognizingPlate.get("timestamp"));

            String plateLicense = resultsFromRecognizingPlate.get("plate");

            if (this.entryService.existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(plateLicense)) {
                return new ResponseEntity<>("Vehicle is already appeared", HttpStatus.OK);
            }

            Vehicle currentVehicle = this.vehicleService.findByLicensePlateNumberIgnoreCase(plateLicense)
                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with plate: " + plateLicense));

            result.put("vehicle", currentVehicle);

            //from vehicle -> get user
            User user = currentVehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.OK);
            }

            result.put("owner", user);

            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.OK);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);
        } else {
            Vehicle currentVehicle = this.vehicleService.getVehicleById(Integer.parseInt(params.get("vehicleId")));
            if (this.entryService.existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(currentVehicle.getPlateLicense())) {
                return new ResponseEntity<>("Vehicle is already appeared", HttpStatus.OK);
            }

            result.put("plate", currentVehicle.getPlateLicense());
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            String timestamp = now.format(formatter);
            result.put("timeIn", this.entryService.convertToDate(timestamp).toString());
            result.put("vehicle", currentVehicle);

            User user = currentVehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.OK);
            }

            result.put("owner", user);

            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) currentVehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.OK);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping(path = "/recognize/in")
    @CrossOrigin
    public ResponseEntity<?> PostRecognizePlateIn(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam() Map<String, String> params) throws IOException {
        //call cloudinary to upload the image and get back url to save
        if (file != null) {
            String inImgUrl = this.cloudService.uploadImage(file);
            params.put("inImgUrl", inImgUrl);
        }

        this.entryService.saveHistoryIn(params);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @PostMapping(path = "/recognize/out/get")
    @CrossOrigin
    public ResponseEntity<?> recognizePlateOut(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "id", required = false) Integer id) throws IOException, ParseException {
        Map<String, Object> result = new HashMap<>();
        //from result got from recognizing -> get vehicle
        Map<String, String> resultsFromRecognizingPlate = this.entryService.recognizePlate(file);

        if (file != null) {
            if (resultsFromRecognizingPlate.get("plate") == null) {
                return new ResponseEntity<>("Cannot get plate license from image!", HttpStatus.OK);
            }
            result.put("plate", resultsFromRecognizingPlate.get("plate"));
            result.put("timeOut", resultsFromRecognizingPlate.get("timestamp"));

            String plateLicense = resultsFromRecognizingPlate.get("plate");
            EntryHistory entry = this.entryService.getEntryHistoryByVehiclePlateLicense(plateLicense);

            if (entry != null) {
                result.put("timeIn", entry.getTimeIn());
                result.put("inImgUrl", entry.getPlateImgIn());
                result.put("vehicle", entry.getVehicle());
                result.put("licensePlateNum", plateLicense);
                result.put("owner", entry.getVehicle().getUser());
            } else {
                return new ResponseEntity<>("No entered vehicle found!", HttpStatus.OK);
            }
//            Vehicle currentVehicle = this.vehicleService.findByLicensePlateNumberIgnoreCase(plateLicense)
//                    .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with plate: " + plateLicense));
//
//            //check if vehicle is correct comparing to the one in In entry history
//            if (this.entryService.existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(plateLicense)) {
//                result.put("vehicle", currentVehicle);
//                result.put("licensePlateNum", plateLicense);
//            } else {
//                return new ResponseEntity<>("No entered vehicle found!", HttpStatus.OK);
//            }

            //from vehicle -> get user
//            User user = currentVehicle.getUser();
//            if (user == null) {
//                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.OK);
//            }
//            result.put("owner", user);
            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) entry.getVehicle().getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.OK);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);

        } else {
            int historyId = id;
            EntryHistory history = this.entryService.getEntryHistoryById(historyId);
            result.put("inImgUrl", history.getPlateImgIn());
            result.put("plate", history.getVehicle().getPlateLicense());
            result.put("timeIn", history.getTimeIn().toString());
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            String timestamp = now.format(formatter);
//            result.put("timeOut", LocalDateTime.now());
            result.put("timeOut", this.entryService.convertToDate(timestamp).toString());

            Vehicle vehicle = history.getVehicle();
            result.put("vehicle", vehicle);
            result.put("licensePlateNum", vehicle.getPlateLicense());

            //from vehicle -> get user
            User user = vehicle.getUser();
            if (user == null) {
                return new ResponseEntity<>("User not found! Please check your infomation", HttpStatus.OK);
            }

            result.put("owner", user);

            //get ticket from vehicle and compare with vehicle's owner
            //a vehicle may have tickets, get the last ticket thats available.
            Date currentDate = new Date();
            List<Ticket> validTicketList = this.ticketService
                    .findValidTicketsByVehicleIdAndDate((long) vehicle.getId(), currentDate);
            if (validTicketList.isEmpty()) {
                return new ResponseEntity<>("No valid ticket found!", HttpStatus.OK);
            }

            //take 1st ticket in the list as the list only has 1 info row
            Ticket currentTicket = validTicketList.get(0);

            result.put("ticket", currentTicket);

        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping(path = "/recognize/out")
    @CrossOrigin
    public ResponseEntity<?> PostRecognizePlateOut(@RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam() Map<String, String> params) throws IOException {
        //call cloudinary to upload the image and get back url to save
        if (file != null) {
            String outImgUrl = this.cloudService.uploadImage(file);
            params.put("outImgUrl", outImgUrl);
        }

        //kiểm tra xác thực xe
        this.entryService.saveHistoryOut(params);

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping(path = "/recognize/out/list")
    @CrossOrigin
    public ResponseEntity<?> inEntryList(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(this.entryService.findEntryHistoriesByTimeOutAndPlateImgOut(params.get("plateLicense")));
    }

    @GetMapping("/count-by-month")
    public Map<String, Long> countByMonth(@RequestParam("month") int month, @RequestParam("year") int year) {
        long count = this.entryService.countByMonth(month, year);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return response;
    }

    @GetMapping("/average-parking-duration")
    public Map<String, Double> getAverageParkingDuration() {
        Double averageDuration = this.entryService.findAverageParkingDuration();
        Map<String, Double> response = new HashMap<>();
        response.put("averageDuration", averageDuration);
        return response;
    }

    @GetMapping("/day")
    public ResponseEntity<Map<String, String>> getDailyStats(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(entryService.countEntriesByDay(date));
    }

    //-------------------------------------------------------------------------
    @PostMapping(path = "/recognize/in/record")
    @CrossOrigin
    public ResponseEntity<?> recordVehicleIn(@RequestParam(value = "file1", required = false) MultipartFile plateImgFile,
            @RequestParam(value = "file2", required = false) MultipartFile personImgFile,
            @RequestParam() Map<String, String> params) throws IOException {

        //check if entry already got recorded IN but no OUT
        Ticket currentTicket = this.ticketService.findByTicketId(params.get("ticketId"));
        if (currentTicket == null) {
            return new ResponseEntity<>("Không tìm thấy thông số vé hợp lệ", HttpStatus.NOT_FOUND);
        }

        //check if not anonymous ticket and date's expired
        Date currentDate = new Date();
        boolean isExpired = this.ticketService.checkTicketDate(currentTicket.getId(), currentDate) == null;
        if (currentTicket.getUserOwned() != this.userService.getAnonymousUser()
                && isExpired) {
            return new ResponseEntity<>("Vé đã hết hạn", HttpStatus.NOT_FOUND);
        }

        EntryHistory currentEntry = this.entryService.getEntryHistoryByTicketId(currentTicket.getId());
        if (currentEntry != null) {
            return new ResponseEntity<>("Phương tiện hiện đã trong bãi và chưa khỏi bãi xe",
                    HttpStatus.NOT_FOUND);
        }

        if (plateImgFile == null || personImgFile == null) {
            return new ResponseEntity<>("Vui lòng cập nhật mục hình ảnh", HttpStatus.BAD_REQUEST);
        }
        
        //if anonymous ticket -> assign new position to the ticket
        if (currentTicket.getUserOwned() == this.userService.getAnonymousUser()) {
            Position availablePosition = this.posService.findFirstAvailablePositionOrdered().get(0);
            if (availablePosition == null) {
                return new ResponseEntity<>("Hiện bãi đã hết vị trí trống", HttpStatus.NOT_FOUND);
            }

            this.posService.assignPositionToTicket(currentTicket.getId(),
                    availablePosition.getId());

        }

        //call cloudinary to upload the image and get back url to save
        if (plateImgFile != null) {
            Map<String, String> plateRecognizeResult = this.entryService.recognizePlate(plateImgFile);

            //check if it's a riel plate img
            if (plateRecognizeResult.get("plate") == null) {
                return new ResponseEntity<>("Không thể phân tích được hình ảnh biển số xe",
                        HttpStatus.NOT_FOUND);
            }

            String inImgUrl = this.cloudService.uploadImage(plateImgFile);
            params.put("inImgUrl", inImgUrl);

            if (currentTicket.getUserOwned() == this.userService.getAnonymousUser()) { //if this ticket is for anonymous user
                if (!plateRecognizeResult.isEmpty()) {
                    params.put("plateNumber", plateRecognizeResult.get("plate"));
                }
            } else if (!currentTicket.getLicenseNumber().equalsIgnoreCase("0")
                    && !currentTicket.getLicenseNumber().equalsIgnoreCase(plateRecognizeResult.get("plate"))) {
                return new ResponseEntity<>("Biển số không khớp với dữ liệu",
                        HttpStatus.CONFLICT);
            } else {
                if (!plateRecognizeResult.isEmpty()) {
                    params.put("plateNumber", plateRecognizeResult.get("plate"));
                }
            }
        }
        if (personImgFile != null) {

            //func to check if its a human face
            Map<String, String> detectingResult = this.entryService.processDetectFace(personImgFile);
            if (Integer.parseInt(detectingResult.get("face_num")) == 1) { //chỉ tồn tại 1 người 1 ảnh
                String inImgUrl = this.cloudService.uploadImage(personImgFile);
                params.put("personImgIn", inImgUrl);
            } else {
                return new ResponseEntity<>("Không nhận diện được khuôn mặt. Hãy thử lại", HttpStatus.CONFLICT);
            }

        }

        this.entryService.recordVehicleIn(params);

        return new ResponseEntity<>("Done", HttpStatus.CREATED);
    }

    @PostMapping(path = "/recognize/out/record")
    @CrossOrigin
    public ResponseEntity<?> recordVehicleOut(@RequestParam(value = "file1", required = false) MultipartFile plateImgFile,
            @RequestParam(value = "file2", required = false) MultipartFile personImgFile,
            @RequestParam() Map<String, String> params) throws IOException, Exception {
        //check ticket valid
        //compare 2 plates
        //save the record
        //attributes are entryId, personImgOut, vehicleImgOut, timeOut
        Map<String, String> result = new HashMap<>();

        //check ticket valid
        Ticket currentTicket = this.ticketService.findByTicketId(params.get("ticketId"));
        Date currentDate = new Date();
        if (this.ticketService.checkTicketDate(currentTicket.getId(), currentDate) != null) {
            if (!currentTicket.getIsPaid()) {
                result.put("Error: ", "Vé chưa thanh toán");
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else { //paid ticket
                EntryHistory currentEntry = this.entryService.getEntryHistoryByTicketId(currentTicket.getId());

                if (currentEntry == null) {
                    result.put("Error: ", "Không có lịch sử ghi nhận xe vào");
                    return new ResponseEntity<>(result, HttpStatus.OK);
                } else {
                    if (plateImgFile != null) {
                        Map<String, String> plateRecognizeResult = this.entryService.recognizePlate(plateImgFile);
                        String plateNumber = plateRecognizeResult.get("plate");

                        if (plateNumber == null) {
                            return new ResponseEntity<>("Không thể phân tích được hình ảnh biển số xe",
                                    HttpStatus.NOT_FOUND);
                        }

                        //check if 2 plates have a similar number               
                        if (plateNumber.equalsIgnoreCase(currentTicket.getLicenseNumber())) {
                            String outImgUrl = this.cloudService.uploadImage(plateImgFile);
                            params.put("outImgUrl", outImgUrl);
                        } else {
                            result.put("Plate Error: ", "Biển số không trùng");
                        }

                    } else {
                        result.put("Plate Error: ", "Không tìm thấy ảnh biển số xe");

                    }
                    if (personImgFile != null) {
                        DownloadImageAsMultipartFile downloadFile = new DownloadImageAsMultipartFile();
                        String personImgInUrl = currentEntry.getPersonImgIn();
                        MultipartFile personImgInFile = downloadFile.downloadImageAsMultipartFile(personImgInUrl);

                        Map<String, ?> comparingResult = this.entryService.processComparingImg(personImgInFile, personImgFile);

                        //System.out.println(comparingResult.get("isSamePerson").toString().equals("true"));
                        //check if same person
                        if (comparingResult.get("isSamePerson") == null) {
                            return new ResponseEntity<>("Không thể phân tích được hình ảnh khách hàng",
                                    HttpStatus.NOT_FOUND);
                        }

                        if (comparingResult.get("isSamePerson").toString().equals("true")) {
                            String outImgUrl = this.cloudService.uploadImage(personImgFile);
                            params.put("personImgOut", outImgUrl);
                        } else {
                            result.put("Person Error: ", "Không phải cùng một người");
                        }
                    } else {
                        result.put("Person Error: ", "Không tìm thấy ảnh khách hàng");
                    }

                    if (result.containsKey("Person Error: ") || result.containsKey("Plate Error: ") || result.containsKey("Error: ")) {
                        return new ResponseEntity<>(result, HttpStatus.OK);
                    }

                    if (params.containsKey("outImgUrl") && params.containsKey("personImgOut")) {
                        this.entryService.recordVehicleOut(params);
                    }

                }

            }
        } else {
            result.put("Error: ", "Vé đã hết hạn");
        }

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping(path = "/entry/ticket/process")
    @CrossOrigin
    public ResponseEntity<?> processTicket(@RequestParam(required = true) Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        Date currentDate = new Date();
        String ticketIdString = params.get("ticketId");

        if (ticketIdString == null) {
            result.put("Ticket error: ", "Mã vé không hợp lệ.");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        Ticket currentTicket = this.ticketService.findByTicketId(ticketIdString);

        if (currentTicket == null) {
            result.put("Ticket error: ", "Không thể tìm thấy vé.");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if (currentTicket.getUserOwned() != this.userService.getAnonymousUser()
                && !currentTicket.getActive()) {
            result.put("Ticket error: ", "Vé đã hết hiệu lực. Vui lòng đăng ký vé mới");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        if (this.ticketService.checkTicketDateValid(currentTicket.getId(), currentDate) == false
                && currentTicket.getUserOwned() != this.userService.getAnonymousUser()) {
            result.put("Ticket error: ", "Vé đã hết hạn.");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        result.put("ownerUsername", currentTicket.getUserOwned().getName());
        result.put("ownerUserId", currentTicket.getUserOwned().getId());
        result.put("ticketType", currentTicket.getTicketType().getType());
        result.put("ticketStatus", String.valueOf(currentTicket.getIsPaid()));
        result.put("ticketTotalPrice", currentTicket.getTotalPrice());
        result.put("ticketStartDate", currentTicket.getStartDay());
        result.put("ticketExpiredDate", currentTicket.getEndDay());
        result.put("ticketVehicleId", currentTicket.getVehicle().getId());
        result.put("ticketVehiclePlateNumber", currentTicket.getVehicle().getPlateLicense());

        String ticketPosition = "";
        if (currentTicket.getFloor() != null && currentTicket.getLine() != null && currentTicket.getPosition() != null) {
            ticketPosition = "Tầng " + currentTicket.getFloor().getFloorNumber()
                    + " dãy " + currentTicket.getLine().getLine()
                    + " vị trí " + currentTicket.getPosition().getPosition();
        }
        result.put("ticketPosition", ticketPosition);

        result.put("ticketLicenseNumber", Objects.toString(currentTicket.getLicenseNumber(), ""));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(path = "/entry/ticket/get/all")
    @CrossOrigin
    public ResponseEntity<?> returnTicketAndEntryInInfo(@RequestParam(required = true) Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        Date currentDate = new Date();

        Ticket currentTicket = this.ticketService.findByTicketId(params.get("ticketId"));

        if (currentTicket == null) {
            result.put("Error: ", "Không thể tìm thấy vé");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        if (this.ticketService.checkTicketDateValid(currentTicket.getId(), currentDate) == false) {
            result.put("Error: ", "Vé đã hết hạn.");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        EntryHistory currentEntry = this.entryService.getEntryHistoryByTicketId(currentTicket.getId());
        if (currentEntry == null) {
            result.put("Error: ", "Không tìm thấy lịch sử tương ứng");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        result.put("ticketId", currentTicket.getId());
        result.put("ownerUsername", currentTicket.getUserOwned().getName());
        result.put("ownerUserId", currentTicket.getUserOwned().getId());
        result.put("ticketType", currentTicket.getTicketType().getType());
        result.put("ticketStatus", String.valueOf(currentTicket.getIsPaid()));
        result.put("ticketTotalPrice", currentTicket.getTotalPrice());
        result.put("ticketStartDate", currentTicket.getStartDay());
        result.put("ticketExpiredDate", currentTicket.getEndDay());
        result.put("ticketVehicleId", currentTicket.getVehicle().getId());
        result.put("ticketVehiclePlateNumber", currentTicket.getVehicle().getPlateLicense());

        String ticketPosition = "";
        if (currentTicket.getFloor() != null && currentTicket.getLine() != null && currentTicket.getPosition() != null) {
            ticketPosition = "Tầng " + currentTicket.getFloor().getFloorNumber()
                    + " dãy " + currentTicket.getLine().getLine()
                    + " vị trí " + currentTicket.getPosition().getPosition();
        }
        result.put("ticketPosition", ticketPosition);

        result.put("ticketLicenseNumber", Objects.toString(currentTicket.getLicenseNumber(), ""));

        //Entry: time in, img in url, person img in url       
        result.put("timeIn", currentEntry.getTimeIn());
        result.put("plateImgInUrl", currentEntry.getPlateImgIn());
        result.put("personImgInUrl", currentEntry.getPersonImgIn());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(path = "/analyzePlate")
    @CrossOrigin
    public ResponseEntity<?> analyzeLicensePlateFromImage(@RequestParam(value = "file", required = false) MultipartFile file)
            throws IOException, ParseException {
        Map<String, String> resultsFromRecognizingPlate = new HashMap<>();

        if (file != null) {
            resultsFromRecognizingPlate = this.entryService.recognizePlate(file);

            if (resultsFromRecognizingPlate.get("plate") == null) {
                return new ResponseEntity<>("Có lỗi xảy ra trong quá trình phân tích\n"
                        + "Có thể là do biển số xe mờ hoặc hệ thống gặp lỗi!", HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(resultsFromRecognizingPlate, HttpStatus.OK);
    }

    @GetMapping("/entry/list")
    public ResponseEntity<Page<EntryHistory>> getEntryHistoryByLicensePlateNumberPageable(@RequestParam Map<String, String> params) {
        String licenseNumber = params.getOrDefault("licenseNumber", "");
        int page = Integer.parseInt(params.getOrDefault("page", "0"));

        Pageable pageable = PageRequest.of(page, pageSize);

        return new ResponseEntity<>(this.entryService.getEntryHistoryListByLicensePlateNumberPageable(licenseNumber, pageable), HttpStatus.OK);
    }

    @GetMapping("/entry/{entryId}/ticket/getTicketId")
    public ResponseEntity<String> getTicketIdFromEntry(@PathVariable("entryId") int entryId) {
        EntryHistory currentEntry = this.entryService.getEntryHistoryById(entryId);
        if (currentEntry == null) {
            return new ResponseEntity<>("Không tìm thấy lịch sử cụ thể", HttpStatus.NOT_FOUND);
        }

        String ticketId = currentEntry.getTicket().getTicketId();
        return new ResponseEntity<>(ticketId, HttpStatus.OK);
    }

    @GetMapping("/entry/all/list")
    public ResponseEntity<Page<EntryHistory>> getAllEntryHistoryByLicensePlateNumberPageable(@RequestParam Map<String, String> params) {
        String licenseNumber = params.getOrDefault("licenseNumber", "");
        int page = Integer.parseInt(params.getOrDefault("page", "0"));

        Pageable pageable = PageRequest.of(page, pageSize);

        return new ResponseEntity<>(this.entryService.getAllEntryHistoryListByLicensePlateNumberPageable(licenseNumber, pageable), HttpStatus.OK);
    }
    
    
    @PostMapping("/entry/image/ticket/get")
    public ResponseEntity<?> getTicketIdFromTicketImage(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException{
        Map<String,?> result = this.textExtractor.extractTextFromImg(file);
        String ticketId = result.get("text").toString();
        if (ticketId.isBlank() || ticketId.isEmpty())
            return new ResponseEntity<>("Không tìm thấy chuỗi ký tự, hãy thử lại!", HttpStatus.CONFLICT);
        return new ResponseEntity<>(ticketId.trim(), HttpStatus.OK);
    }
}
