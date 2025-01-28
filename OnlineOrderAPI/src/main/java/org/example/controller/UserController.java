package org.example.controller;

import org.example.model.User;
import org.example.model.Address;
import org.example.repository.UserRepository;
import org.example.repository.AddressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, AddressRepository addressRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Tüm Kullanıcıları Listeleme
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Yeni Kullanıcı Ekleme
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Şifre eksik!");
        }

        // Şifreyi hashleyerek kaydetme
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Kullanıcı başarıyla oluşturuldu. ID: " + user.getId());
    }


    // ✅ Kullanıcı Bilgilerini Güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }

        User user = optionalUser.get();

        // Eğer isim, e-posta veya şifre güncellenmek isteniyorsa güncelle.
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(user);
        return ResponseEntity.ok("Kullanıcı başarıyla güncellendi.");
    }

    // ✅ Kullanıcı Silme
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Kullanıcıya Adres Ekleme
    @PostMapping("/{id}/address")
    public ResponseEntity<?> addAddressToUser(
            @PathVariable Long id,
            @RequestBody Address address) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            address.setUser(user);

            try {
                addressRepository.save(address);
                return ResponseEntity.ok(address);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Adres eklerken hata oluştu: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    // ✅ Kullanıcının Adreslerini Listeleme
    @GetMapping("/{id}/address")
    public ResponseEntity<?> getUserAddresses(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAddresses().isEmpty()) {
                return ResponseEntity.ok("Bu kullanıcı için kayıtlı adres bulunmamaktadır.");
            }
            return ResponseEntity.ok(user.getAddresses());
        } else {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }
}
