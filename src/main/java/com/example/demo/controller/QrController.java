package com.example.demo.controller;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class QrController {

    // HTMLページ表示
    @GetMapping("/qr")
    public String qrPage() {
        return "qr"; // qr.html
    }

    // QRコード画像を出力
    @GetMapping(value = "/qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public void generateQrImage(HttpServletResponse response) throws IOException {
        String url = "http://172.31.99.195:8080/receivetime\n"
        		+ "";
        int width = 300;
        int height = 300;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            OutputStream outputStream = response.getOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
