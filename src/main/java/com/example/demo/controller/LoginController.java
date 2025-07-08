package com.example.demo.controller;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

 // ログイン処理
    @PostMapping("/login")
    public String login(@RequestParam String storeName, @RequestParam String password, HttpSession session) {
        boolean success = loginService.login(storeName, password);
        if (success) {
            session.setAttribute("storeName", storeName); // 店舗名をセッションに保存
            return "redirect:/home";
        } else {
            return "login-failure";
        }
    }

    // ログインフォーム表示
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // ホーム画面
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    // 注文リスト表示
    @GetMapping("/orderlist")
    public String showOrderList(Model model) {
        List<OrderManagement> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "orderlist";
    }

    // 商品登録フォーム表示
    @GetMapping("/form")
    public String showProductForm(Model model, HttpSession session) {
        model.addAttribute("product", new Product());

        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) {
            return "redirect:/login";
        }
        model.addAttribute("storeName", storeName); // 画面に店舗名を渡す
        return "form";
    }
    
    @PostMapping("/register")
public String registerProduct(@RequestParam("productName") String name,
                              @RequestParam("price") BigDecimal price,
                              @RequestParam("storeName") String storeName,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) {
    try {
        // ① S3 にアップロード
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String key      = "products/" + fileName;   // バケット内パス

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket("product-images")           // バケット名
                .key(key)
                .contentType(imageFile.getContentType())
                .build();

        supabaseS3.putObject(
            putReq,
            RequestBody.fromInputStream(imageFile.getInputStream(),
                                        imageFile.getSize()));

        // ② 公開 URL を組み立てて DB に保存
        String publicUrl = String.format(
            "https://%s.supabase.co/storage/v1/object/public/product-images/%s",
            projectRef, key);

        Product product = new Product();
        product.setProductName(name);
        product.setPrice(price);
        product.setStoreName(storeName);
        product.setImageUrl(publicUrl);

        productRepository.save(product);

        model.addAttribute("message", "商品が正常に登録されました！");
        return "register_success";

    } catch (IOException | S3Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "画像アップロードに失敗しました。");
        return "form";
    }
}
    
    
 // 商品一覧画面（ログイン店舗の商品だけ表示）
    @GetMapping("/productlist")
    public String showProductList(Model model, HttpSession session) {
        String storeName = (String) session.getAttribute("storeName"); // セッションから店舗名取得

        if (storeName == null) {
            return "redirect:/login"; // 未ログインならログイン画面へ
        }

        List<Product> productList = productRepository.findByStoreName(storeName); // 店舗名で絞る
        model.addAttribute("productList", productList);
        return "productlist";
    }

    @GetMapping("/product/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/productlist"; // 商品がなければリストへ
        }
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
            if (product == null) {
                return "redirect:/productlist";
            }

            product.setProductName(productName);
            product.setPrice(price);
            product.setStoreName(storeName);

            if (!imageFile.isEmpty()) {
                // 新しい画像を保存
                String uploadDir = "C:/uploaded-image/";
                String fileName = imageFile.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                imageFile.transferTo(dest);

                product.setImageUrl(fileName); // 新しいファイル名を保存
            } else {
                product.setImageUrl(oldImageUrl); // 画像変更なしの場合、元のまま
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
        // セッションから店舗名取得
        String storeName = (String) session.getAttribute("storeName");
        if (storeName == null) {
            return "redirect:/login"; // 未ログインならログイン画面へ
        }
        
        // 商品取得
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // 削除しようとしている商品がログイン中の店舗の商品か確認（セキュリティ対策）
            if (storeName.equals(product.getStoreName())) {
                productRepository.delete(product);
            }
        }
        
        // 削除後は商品一覧画面へリダイレクト
        return "redirect:/productlist";
    }
    
 // 店舗登録フォームの表示
    @GetMapping("/storeregistration")
    public String showStoreRegistrationForm(Model model) {
        model.addAttribute("store", new Store());
        return "storeregistration";
    }

    // 店舗登録処理
    @PostMapping("/storeregister")
    public String registerStore(@ModelAttribute Store store, Model model) {
        storeRepository.save(store);
        model.addAttribute("message", "店舗を登録しました！");
        return "login"; // 登録後はログインページへ遷移（任意で変更可）
    }
  
 // ログアウト
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login"; 
    }
    
}
