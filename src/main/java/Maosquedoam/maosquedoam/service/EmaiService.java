package Maosquedoam.maosquedoam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmaiService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacao(String destinatario, String codigo){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recuperação de senha - Mãos que Doam");
        message.setText("Seu código de recuperação de senha é: " + codigo + "\n\nEste código expira em 15 minutos." +
                "\n\nSe você não solicitou a recuperação de senha, ignore este email.");
        mailSender.send(message);
    }
}
