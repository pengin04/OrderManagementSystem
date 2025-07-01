package com.example.demo.service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Mail;
import com.example.demo.form.MailForm;
import com.example.demo.repository.MailRepository;


@Service
public class MailService {
	@Autowired
	private MailRepository mailRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	@Value("$spring.mail.username")
	private String fromAddress;
	@Async
	public void sendMail(String to, String subject, String content) {
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(to);
	    message.setSubject(subject);
	    message.setText(content);
	    mailSender.send(message);
	}
	
	@Async
	public void sendOrderConfirmationMail(String toEmail, String name, String orderDetails, int totalPrice) {
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(toEmail);
	    message.setSubject("【注文確認】ご注文ありがとうございました");
	    message.setText("以下の内容でご注文を承りました。\n\n" +
	                    "お名前: " + name + "\n" +
	                    "注文内容:\n" + orderDetails + "\n" +
	                    "合計金額: ¥" + totalPrice + "\n\n" +
	                    "ご利用いただきありがとうございます。\n\n" +
	                    "本メールは自動送信されています。");

	    mailSender.send(message);
	}



	//入力項目をDBに保存
	@Async
	public void insertMail(MailForm mailForm) {
		Mail mail = new Mail();
		
		//確定を押した日時を取得
		Timestamp enableDateTime = new Timestamp(System.currentTimeMillis());

		// 認証コードを生成
		String verificationCode = generateVerificationCode();

		// メールを送信
		sendVerificationEmail(mailForm.getEmail(), verificationCode);

		mail.setEmail(mailForm.getEmail());
		mail.setAuthenticationCode(verificationCode);
		mail.setCreatedAt(enableDateTime);
		
		mailRepository.save(mail);
	}

	//認証コードの生成
	@Async
	private String generateVerificationCode() {
		Random random = new Random();
		int code = 100000 + random.nextInt(900000);
		return String.valueOf(code);
	}

	//メール送信内容
	@Async
	private void sendVerificationEmail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);
		message.setTo(to);
		message.setSubject("認証コード通知");
		message.setText("以下の認証コードを入力してください。\n\n"
				+ code + "\n\n"
				+ "このメールに覚えのない場合は、メールを削除してください。");
		mailSender.send(message);
	}

     //送信されてきたメールアドレスでrepositoryに送り、最新の情報を取ってくる。
	@Async
	public Mail selectfindOneByEmail(String email) {
		Optional<Mail> mailOptional = mailRepository.findLatestByEmail(email);
		return mailOptional.orElse(null);
	}

	@Async
	 public void sendApplicationMail(String toEmail, String employeeId, String name) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(toEmail);
	        message.setSubject("申請が完了しました");
	        message.setText("従業員ID: " + employeeId + "\n" +
	                        "氏名: " + name + "\n\n" +
	                        "申請を受け付けました。管理者が確認後、通知を送ります。");

	        mailSender.send(message);
	    }
	@Async
	 public void sendApprovalMail(String to, String employeeId, String name) {
		    SimpleMailMessage message = new SimpleMailMessage();
		    message.setTo(to);
		    message.setSubject("【承認通知】申請が承認されました");
		    message.setText("以下の申請が承認されました。\n\n"
		                    + "社員ID: " + employeeId + "\n"
		                    + "氏名: " + name + "\n\n"
		                    + "ご確認ください。");

		    mailSender.send(message);
		}
	 
	// 却下通知メールを送信
	 public void sendRejectionMail(String to, String employeeId, String name) {
	     SimpleMailMessage message = new SimpleMailMessage();
	     message.setTo(to);
	     message.setSubject("【却下通知】申請が却下されました");
	     message.setText("以下の申請が却下されました。\n\n"
	                     + "社員ID: " + employeeId + "\n"
	                     + "氏名: " + name + "\n\n"
	                     + "申請内容に問題がある場合は、管理者にお問い合わせください。");

	     mailSender.send(message);
	 }


}

