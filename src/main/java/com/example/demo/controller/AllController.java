package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Mail;
import com.example.demo.form.MailForm;
import com.example.demo.service.MailService;

@Controller
public class AllController {

    //後に作成するMailServiceの呼び出し
	@Autowired
	private MailService mailService;

	@GetMapping("/index")
	public String showHome(MailForm mailForm) {
		return "index";
	}

	@PostMapping("/sendMail")
	public String sendMail(@Validated MailForm mailForm, BindingResult bindingResult) {
		//バリデーションチェック
		if(!bindingResult.hasErrors()) {
			mailService.insertMail(mailForm); ///////////////////////[1]
			return "code_check";
		}else {
			return showHome(mailForm);
		}
	}
	
	@PostMapping("/codeCheck")
	public String codeCheck(
			@RequestParam("email")String email, 
			@RequestParam("code")String code, 
			Model model) {
		
		Mail mail = mailService.selectfindOneByEmail(email);//////////////////[2]
		
		//入力されたメアドが存在しなかった場合
		if(mail == null) {
			model.addAttribute("msg", "メールアドレスまたは認証コードに誤りがあります。");
			return "code_check";
		}
		
		//入力されたメアドのデータの認証コードと照合
		if(mail.getAuthenticationCode().equals(code)) {
			return "passwordUpdate";
		}else {
			model.addAttribute("msg", "メールアドレスまたは認証コードに誤りがあります。");
			return "code_check";
		}
	}

	@GetMapping("/back")
	public String back(MailForm mailForm) {
		return showHome(mailForm);
	}
}
