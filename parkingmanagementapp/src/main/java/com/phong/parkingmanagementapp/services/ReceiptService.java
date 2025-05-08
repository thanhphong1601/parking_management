/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phong.parkingmanagementapp.services;

import com.phong.parkingmanagementapp.models.Receipt;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author Admin
 */
public interface ReceiptService {
    Page<Receipt> getReceipt(Pageable pageale, String userName, String ticketId);
    boolean saveReceipt(Receipt receipt);
    Receipt getReceiptByTicketId(int id);
    Page<Receipt> getCustomerReceipt(Pageable pageale, int userId,
            Date startDay, Date endDay);
    long sumTotalRevenue();
    List<Object[]> getMonthlyRevenue();
}
