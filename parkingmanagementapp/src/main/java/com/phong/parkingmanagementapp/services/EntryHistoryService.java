/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.EntryHistory;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface EntryHistoryService {

    Map<String, String> recognizePlate(MultipartFile file) throws IOException;

    File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException;

    Date convertToDate(String timestampStr) throws ParseException;

    void saveHistoryIn(Map<String, String> attributes);

    void saveHistoryOut(Map<String, String> attributes);

    boolean existsByLicensePlateNumberAndTimeInAndPlateImgOutNull(String licensePlateNumber);

    List<EntryHistory> findEntryHistoriesByTimeOutAndPlateImgOut(String licensePlate);

    EntryHistory getEntryHistoryById(int id);

    EntryHistory getEntryHistoryByVehiclePlateLicense(String plateLicense);

    long countByDate(Date date);

    long countByMonth(int month, int year);

    Double findAverageParkingDuration();

}
