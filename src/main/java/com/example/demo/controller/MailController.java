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
    @PostMapping("/order/send-mail")
    public String sendOrderReadyMail(
            @RequestParam String email,
            @RequestParam String customerName,
            @RequestParam String itemDetails,
            @RequestParam int totalPrice,
            Model model) {

        // 定型文送信
        String content = "以下の内容でご注文を承りました。\n\n" +
                         "お名前: " + customerName + "\n" +
                         "注文内容:\n" + itemDetails + "\n" +
                         "合計金額: ¥" + totalPrice + "\n\n" +
                         "商品をご準備できましたので、受け取り可能です。\n\n" +
                         "本メールは自動送信されています。";

        mailService.sendOrderConfirmationMail(email, customerName, itemDetails, totalPrice);

        model.addAttribute("successMessage", customerName + "様にメールを送信しました。");

        // 再度注文一覧ページに戻る
        return "redirect:/orderlist";
    }

}
