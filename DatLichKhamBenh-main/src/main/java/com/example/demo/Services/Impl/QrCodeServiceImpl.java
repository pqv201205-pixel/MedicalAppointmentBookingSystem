package com.example.demo.Services.Impl;

import com.example.demo.Services.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;


@Service
public class QrCodeServiceImpl implements QrCodeService{

    // Trả về chuỗi ảnh Base64 để hiển thị trực tiếp ở frontend mà không cần lưu ổ đĩa cứng
    @Override
    public String generateAppointmentQrCode(Integer appointmentId) throws Exception {
        String qrContent = "DATLICHKHAM_APPOINTMENT_ID_" + appointmentId;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}