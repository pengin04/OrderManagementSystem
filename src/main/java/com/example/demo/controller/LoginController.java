package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.ContentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.OrderManagement;
import com.example.demo.entity.Product;
import com.example.demo.entity.Store;
import com.example.demo.repository.OrderManagementRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StoreRepository;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private OrderManagementRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Value("${supabase.project-ref}")
    private String projectRef;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.secret-key}")  // ← これに合わせる
private String secretKey;

    @PostMapping("/login")
    public String login(@RequestParam String storeName, @RequestParam String password, HttpSession session) {
        boolean success = loginService.login(storeName, password);
        if (success) {
            session.setAttribute("storeName", storeName);
            return "redirect:/home";
        } else {
            return "login-failure";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/orderlist")
    public String showOrderList(Model model) {
        List<OrderManagement> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "orderlist";
    }

    @GetMapping("/form")
    public String showProductForm(Model model, HttpSession session) {
        model.addAttribute("product", new Product());
        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) {
            return "redirect:/login";
        }
        model.addAttribute("storeName", storeName);
        return "form";
    }

    @PostMapping("/register")
public String registerProduct(@RequestParam("productName") String name,
                              @RequestParam("price") BigDecimal price,
                              @RequestParam("storeName") String storeName,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) {
    try {
        // ファイル名をユニークに生成
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String objectPath = "products/" + fileName;

        // Supabase Storage REST API アップロードURL
        String uploadUrl = String.format(
    "https://%s.supabase.co/storage/v1/object/%s?path=%s",
    projectRef, bucket, objectPath
);


        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut put = new HttpPut(uploadUrl);
            put.setHeader("Authorization", "Bearer " + secretKey);
            String contentType = imageFile.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream"; // fallback
            }
            put.setHeader("Content-Type", contentType);

            InputStreamEntity entity = new InputStreamEntity(
                    imageFile.getInputStream(),
                    imageFile.getSize(),
                    ContentType.parse(contentType)
            );
            put.setEntity(entity);

            var response = httpClient.execute(put);
            int statusCode = response.getCode();
            if (statusCode != 200 && statusCode != 201) {
                model.addAttribute("error", "Supabaseアップロード失敗: " + statusCode);
                return "form";
            }
        }

        // 公開URLの生成
        String publicUrl = String.format("https://%s.supabase.co/storage/v1/object/public/%s/%s",
                projectRef, bucket, objectPath);

        // DBに商品登録
        Product product = new Product();
        product.setProductName(name);
        product.setPrice(price);
        product.setStoreName(storeName);
        product.setImageUrl(publicUrl);

        productRepository.save(product);
        model.addAttribute("message", "商品が正常に登録されました！");
        return "register_success";

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "画像アップロードに失敗しました: " + e.getMessage());
        return "form";
    }
}


    @GetMapping("/productlist")
    public String showProductList(Model model, HttpSession session) {
        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) return "redirect:/login";
        List<Product> productList = productRepository.findByStoreName(storeName);
        model.addAttribute("productList", productList);
        return "productlist";
    }

    @GetMapping("/product/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/productlist";
        model.addAttribute("product", product);
        return "productedit";
    }

    @PostMapping("/product/update")
    public String updateProduct(@RequestParam("id") int id,
                                @RequestParam("productName") String productName,
                                @RequestParam("price") BigDecimal price,
                                @RequestParam("storeName") String storeName,
                                @RequestParam("oldImageUrl") String oldImageUrl,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) return "redirect:/productlist";

            product.setProductName(productName);
            product.setPrice(price);
            product.setStoreName(storeName);

            if (!imageFile.isEmpty()) {
                String uploadDir = "C:/uploaded-image/";
                String fileName = imageFile.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                imageFile.transferTo(dest);
                product.setImageUrl(fileName);
            } else {
                product.setImageUrl(oldImageUrl);
            }

            productRepository.save(product);
            return "redirect:/productlist";

        } catch (IOException e) {
            e.printStackTrace();
            return "productedit";
        }
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam("id") int id, HttpSession session) {
        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) return "redirect:/login";
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && storeName.equals(product.getStoreName())) {
            productRepository.delete(product);
        }
        return "redirect:/productlist";
    }

    @GetMapping("/storeregistration")
    public String showStoreRegistrationForm(Model model) {
        model.addAttribute("store", new Store());
        return "storeregistration";
    }

    @PostMapping("/storeregister")
    public String registerStore(@ModelAttribute Store store, Model model) {
        storeRepository.save(store);
        model.addAttribute("message", "店舗を登録しました！");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
