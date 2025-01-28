package org.example.controller;

import org.example.model.Order;
import org.example.model.User;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // ✅ Sipariş Ekleme
    @PostMapping("/{userId}")
    public ResponseEntity<?> createOrder(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı. ID: " + userId);
        }

        try {
            User user = optionalUser.get();
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("Pending");
            orderRepository.save(order);
            return ResponseEntity.ok("Sipariş başarıyla oluşturuldu. Sipariş ID: " + order.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Sipariş oluşturulurken hata oluştu: " + e.getMessage());
        }
    }



    // ✅ Tüm Siparişleri Listeleme
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ Kullanıcının Siparişlerini Listeleme
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(user.getOrders());
        } else {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı.");
        }
    }

    // ✅ Sipariş Durumunu Güncelleme
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody String newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(newStatus);
            orderRepository.save(order);
            return ResponseEntity.ok("Sipariş durumu güncellendi: " + newStatus);
        } else {
            return ResponseEntity.status(404).body("Sipariş bulunamadı.");
        }
    }

    // ✅ Sipariş Silme
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return ResponseEntity.ok("Sipariş başarıyla silindi.");
        } else {
            return ResponseEntity.status(404).body("Sipariş bulunamadı.");
        }
    }
}
