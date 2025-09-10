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
        
     // ★★★ 修正箇所 1: storeName を allParams から取得し、セッションに保存する ★★★
        String storeName = allParams.get("storeName");
        if (storeName != null) {
            session.setAttribute("selectedStoreName", storeName); // セッションキー名を明確化
        }

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
            @RequestParam String deliveryType,
            @RequestParam(required = false) String deliveryAddress,
            HttpSession session,
            Model model) {

        Integer totalPrice = (Integer) session.getAttribute("total_price");
        if ("delivery".equals(deliveryType)) {
            totalPrice += 100;
            session.setAttribute("deliveryNote", "※配達の場合、別途100円が加算されています。");
        } else {
            session.setAttribute("deliveryNote", null);
        }

        // ★ログイン中の店舗名をセッションから取得
        String storeNameFromSession = (String) session.getAttribute("selectedStoreName"); // セッションキー名を変更

        OrderManagement order = new OrderManagement();
        order.setCustomer_name(lastName + " " + firstName);
        order.setEmail(email);
        order.setPhone_number(phone);
        order.setDeliveryType(deliveryType); // ← 修正済み
        order.setDelivery_address(deliveryAddress);
        order.setItem_details((String) session.getAttribute("item_details"));
        order.setTotal_price(totalPrice);
        order.setOrder_time(LocalDateTime.now());
        order.setPickup_time((LocalDateTime) session.getAttribute("pickup_time"));
        order.setStoreName(storeNameFromSession);

        // ★ここで店舗名をセット
        order.setStoreName(storeNameFromSession);

        orderManagementRepository.save(order);

        model.addAttribute("order", order);

        session.setAttribute("deliveryType", deliveryType);
        session.setAttribute("deliveryAddress", deliveryAddress);
        session.setAttribute("total_price", totalPrice);

        return "confirmation";
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
    public String showPickedUpOrders(Model model, HttpSession session) {
        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) {
            return "redirect:/login";
        }

        List<OrderManagement> pickedUpOrders = 
                orderManagementRepository.findByStoreNameAndPickedUpTrue(storeName);

        model.addAttribute("orders", pickedUpOrders);
        return "orders"; // orders.html
    }

    @GetMapping("/delivery-orders")
    public String showDeliveryOrders(Model model, HttpSession session) {
        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) {
            return "redirect:/login";
        }

        List<OrderManagement> deliveryOrders = 
                orderManagementRepository.findByStoreNameAndDeliveryType(storeName, "delivery");

        model.addAttribute("orders", deliveryOrders);
        return "delivery-orders"; // delivery-orders.html
    }

    
//    @GetMapping("/delivery-orders")
//    public String showDeliveryOrders(Model model) {
//        List<OrderManagement> deliveryOrders = orderManagementRepository.findByDeliveryType("delivery");
//        model.addAttribute("orders", deliveryOrders);
//        return "delivery-orders";
//    }



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

        order.setPickedUp(true);
        order.setCompleted_time(LocalDateTime.now()); // 受け渡し完了日時を保存
        orderManagementRepository.save(order);

        return "redirect:/orderlist";
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
	
//	@GetMapping("/confirmation")
//    public String confirmationPage() {
//        return "confirmation";
//    }
}