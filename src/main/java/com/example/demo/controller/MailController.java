package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.MailService;

@Controller
public class MailController {

    @Autowired
    private MailService mailService;

    // メール送信フォームを表示
    @GetMapping("/mail_form")
    public String showMailForm() {
        return "mail_form"; // `mail_form.html` を表示
    }

    // メール送信処理
    @PostMapping("/mail/send")
    public String sendMail(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String content,
            Model model) {

        try {
            mailService.sendMail(toEmail, subject, content);
            model.addAttribute("successMessage", "メールを送信しました。");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "メールの送信に失敗しました。");
        }

        return "mail_form"; // 再びフォームページを表示
    }
}
