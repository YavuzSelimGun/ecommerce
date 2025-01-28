package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "Giriş Sayfası - Lütfen Kimlik Bilgilerinizi Giriniz.";  // Basit bir yanıt, HTML'e geçilebilir
    }
}
