package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.OrderManagement;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderManagementRepository;
import com.example.demo.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	
	@Autowired
    private ProductRepository productRepository;
	@Autowired
    private OrderManagementRepository orderManagementRepository;
	
	// セッション経由で受け取り日時などを保持するために一時的に使います
       
    @PostMapping("/orderdata")
    public String handleOrderData(@RequestParam Map<String, String> allParams, HttpSession session) {
        StringBuilder itemDetailsBuilder = new StringBuilder();
        int totalPrice = 0;

        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("quantity_")) {
                String product_name = entry.getKey().replace("quantity_", "");
                int quantity = Integer.parseInt(entry.getValue());

                if (quantity > 0) {
                    int price = Integer.parseInt(allParams.get("price_" + product_name));
                    itemDetailsBuilder.append(product_name)
                        .append(" x ")
                        .append(quantity)
                        .append(", ");
                    totalPrice += quantity * price;
                }
            }
        }

        String itemDetails = itemDetailsBuilder.toString();
        if (itemDetails.endsWith(", ")) {
            itemDetails = itemDetails.substring(0, itemDetails.length() - 2);
        }

        session.setAttribute("item_details", itemDetails);
        session.setAttribute("total_price", totalPrice);

        return "redirect:/orderdata";
    }
    
    @PostMapping("/save-order")
    public String saveOrder(
            @RequestParam String lastName,
            @RequestParam String firstName,
            @RequestParam String email,
            @RequestParam String phone,
            HttpSession session) {

        String customerName = lastName + " " + firstName;
        LocalDateTime pickupTime = (LocalDateTime) session.getAttribute("pickup_time");

        String itemDetails = (String) session.getAttribute("item_details");
        Integer totalPrice = (Integer) session.getAttribute("total_price");

        OrderManagement order = new OrderManagement();
        order.setCustomer_name(customerName);
        order.setEmail(email);
        order.setPhone_number(phone);
        order.setPickup_time(pickupTime);
        order.setStore_id(1); // 固定値
        order.setOrdered_item(itemDetails); // 旧フィールドを再利用
        order.setItem_details(itemDetails); // 新カラム（仮名）
        order.setTotal_price(totalPrice);   // 新カラム（仮名）

        orderManagementRepository.save(order);

        return "redirect:/confirmation";
    }


    @GetMapping("/orderdata")
    public String showOrderDataPage() {
        return "orderdata"; // orderdata.htmlに商品情報も表示
    }


    @GetMapping("/receivetime")
    public String receivetimePage() {
        return "receivetime";
    }
    
    @GetMapping("/orders")
    public String showPickedUpOrders(Model model) {
        // pickedUp が true の注文だけ取得
        List<OrderManagement> pickedUpOrders = orderManagementRepository.findByPickedUpTrue();
        model.addAttribute("orders", pickedUpOrders);
        return "orders"; // orders.html を作る
    }


    @GetMapping("/order")
    public String orderPage(Model model) {
        List<Product> products = productRepository.findAll();

        // storeNameごとにグループ化
        Map<String, List<Product>> productsByStore =
                products.stream().collect(Collectors.groupingBy(Product::getStoreName));

        model.addAttribute("productsByStore", productsByStore);
        return "order";
    }
    
    @PostMapping("/order/mark-picked-up")
    public String markPickedUp(@RequestParam("orderId") Integer orderId) {
        OrderManagement order = orderManagementRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));

        order.setPickedUp(true); // ← 受け渡し済みにする
        orderManagementRepository.save(order);

        return "redirect:/orderlist"; // 注文一覧画面に戻す（パスは環境に合わせて調整）
    }



    @PostMapping("/receivetime/save")
    public String savePickupTime(@RequestParam("pickup_datetime") String datetime, HttpSession session) {
        LocalDateTime pickupTime = LocalDateTime.parse(datetime);
        session.setAttribute("pickup_time", pickupTime);
        return "redirect:/order";
    }

	
	@GetMapping("/automaticmail")
    public String automaticmailPage() {
        return "automaticmail";
    }
	
	@GetMapping("/confirmation")
    public String confirmationPage() {
        return "confirmation";
    }
}