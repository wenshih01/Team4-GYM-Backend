package team4.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team4.shopping.model.GymBean;
import team4.shopping.model.ProductsImage;
import team4.shopping.model.ShopService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService sService;

    // 新增商品
    @PostMapping(value = "/save", consumes = {"multipart/form-data"})
    public ResponseEntity<?> saveGym(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("category") String category,
            @RequestParam("status") int status,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        try {
            // 創建商品物件
            GymBean gym = new GymBean();
            gym.setName(name);
            gym.setDescription(description);
            gym.setPrice(price);
            gym.setStockQuantity(stockQuantity);
            gym.setCategory(category);
            gym.setStatus(status);
            gym.setCreatedAt(LocalDateTime.now());
            gym.setUpdatedAt(LocalDateTime.now());

            // 處理圖片上傳
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = saveImages(images);
                List<ProductsImage> productsImages = new ArrayList<>();
                for (String imageUrl : imageUrls) {
                    ProductsImage image = new ProductsImage();
                    image.setImageUrl(imageUrl);
                    image.setGym(gym);
                    productsImages.add(image);
                }
                gym.setImages(productsImages);
            }

            // 保存商品
            GymBean savedGym = sService.saveGym(gym);

            // 返回成功響應
            return ResponseEntity.ok(savedGym);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("商品保存失敗：" + e.getMessage());
        }
    }

    private List<String> saveImages(List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        // 設定存儲目錄
        String uploadDir = new File("uploads/images").getAbsolutePath();

        for (MultipartFile image : images) {
            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // 確保目錄存在
            }

            File dest = new File(uploadDir, filename);
            image.transferTo(dest); // 

            // **圖片存取 URL（前端可直接用）**
            imageUrls.add("/uploads/images/" + filename);
        }

        return imageUrls;
    }




    // 刪除商品
    @DeleteMapping("/delete/{product_id}")
    public ResponseEntity<?> deleteGym(@PathVariable("product_id") Integer id) {
        boolean deleted = sService.deleteGymById(id);
        if (deleted) {
            return ResponseEntity.ok("商品 ID " + id + " 已成功刪除！");
        }
        return ResponseEntity.status(404).body("商品刪除失敗！");
    }

    // 查詢單筆商品
    @GetMapping("/gym/{id}")
    public ResponseEntity<?> findGymById(@PathVariable Integer id) {
        Optional<GymBean> gym = sService.findGymById(id);
        if (gym.isPresent()) {
            GymBean gymBean = gym.get();
            String baseUrl = "http://localhost:8082"; // 根據您的後端地址設置
            if (gymBean.getImages() != null) {
                for (ProductsImage image : gymBean.getImages()) {
                    image.setImageUrl(baseUrl + image.getImageUrl()); // 拼接完整 URL
                }
            }
            return ResponseEntity.ok(gymBean);
        }
        return ResponseEntity.status(404).body("商品未找到");
    }


 // 查詢全部商品
    @GetMapping("/list")
    public ResponseEntity<?> findAllGyms() {
        List<GymBean> gyms = sService.findAllGyms();
        for (GymBean gym : gyms) {
            for (ProductsImage image : gym.getImages()) {
                String fullUrl = "http://localhost:8082" + image.getImageUrl();
                image.setImageUrl(fullUrl); // 更新圖片 URL 為完整路徑
            }
        }
        return ResponseEntity.ok(gyms);
    }



    // 模糊查詢
    @GetMapping("/search")
    public ResponseEntity<?> findGymsByName(@RequestParam(value = "name", required = false) String name) {
        List<GymBean> gyms;

        if (name == null || name.trim().isEmpty()) {
            gyms = sService.findAllGyms();
        } else {
            gyms = sService.findGymsByName(name);
        }

        if (gyms.isEmpty()) {
            return ResponseEntity.status(404).body("未找到匹配的商品！");
        }

        return ResponseEntity.ok(gyms);
    }

    // 更新商品
    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateGym(
            @RequestParam("productId") int productId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("category") String category,
            @RequestParam("status") int status,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        try {
            // 創建更新用的商品實例
            GymBean gym = new GymBean();
            gym.setProductId(productId);
            gym.setName(name);
            gym.setDescription(description);
            gym.setPrice(price);
            gym.setStockQuantity(stockQuantity);
            gym.setCategory(category);
            gym.setStatus(status);

            // 處理圖片上傳
            List<String> imageUrls = null;
            if (images != null && !images.isEmpty()) {
                imageUrls = updateImages(images); // 圖片保存邏輯
            }

            // 更新商品
            GymBean updatedGym = sService.updateGym(gym, imageUrls);
            if (updatedGym != null) {
                return ResponseEntity.ok(updatedGym);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("商品不存在，無法更新！");
            }

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片上傳失敗：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("商品更新失敗：" + e.getMessage());
        }
    }

    private List<String>updateImages(List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        // 設定存儲目錄
        String uploadDir = new File("uploads/images").getAbsolutePath();

        for (MultipartFile image : images) {
            String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // 確保目錄存在
            }

            File dest = new File(uploadDir, filename);
            image.transferTo(dest); // 

            // **圖片存取 URL（前端可直接用）**
            imageUrls.add("/uploads/images/" + filename);
        }

        return imageUrls;
    }






    // 查詢上架或下架商品
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getProductsByStatus(@PathVariable int status) {
        if (status != 0 && status != 1) {
            return ResponseEntity.badRequest().body("狀態必須是 0（下架）或 1（上架）！");
        }

        List<GymBean> products = sService.findProductsByStatus(status);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到任何符合條件的商品！");
        }

        // 為每張圖片生成完整的 URL
        String baseUrl = "http://localhost:8082"; // 根據實際情況修改為您的後端域名
        for (GymBean product : products) {
            if (product.getImages() != null) {
                for (ProductsImage image : product.getImages()) {
                    String fullUrl = baseUrl + image.getImageUrl(); // 拼接完整的 URL
                    image.setImageUrl(fullUrl); // 更新圖片的 URL
                }
            }
        }

        return ResponseEntity.ok(products);
    }


    // 切換商品上下架狀態
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleProductStatus(@PathVariable int id) {
        try {
            GymBean updatedProduct = sService.toggleProductStatus(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 查詢特定分類商品
    @GetMapping("/category/{category}")
    public ResponseEntity<?> findGymsByCategory(@PathVariable("category") String category) {
        List<GymBean> gyms = sService.findGymsByCategory(category);
        if (gyms.isEmpty()) {
            return ResponseEntity.status(404).body("該分類 '" + category + "' 下未找到商品！");
        }
        return ResponseEntity.ok(gyms);
    }
 // 根據商品 ID 更新商品狀態（0=下架, 1=上架, 2=假刪除）
    @PutMapping("/product/{id}/status/{status}")
    public ResponseEntity<?> updateProductStatus(@PathVariable int id, @PathVariable int status) {
        if (status < 0 || status > 2) {
            return ResponseEntity.badRequest().body("狀態必須是 0（下架）、1（上架）或 2（假刪除）！");
        }
        try {
            GymBean updatedProduct = sService.updateProductStatus(id, status);
            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.status(404).body("商品 ID " + id + " 不存在！");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新商品狀態失敗：" + e.getMessage());
        }
    }
    
    // 查詢分類商品
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = sService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(categories);
    }

    //刪除圖片
    @DeleteMapping("/images")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> request) {
        String productIdStr = request.get("productId");
        String imageUrl = request.get("imageUrl");

        System.out.println("收到刪除圖片請求：productId=" + productIdStr + ", imageUrl=" + imageUrl);

        if (productIdStr == null || imageUrl == null) {
            return ResponseEntity.badRequest().body("缺少必要參數 productId 或 imageUrl");
        }

        try {
            int productId = Integer.parseInt(productIdStr);

            // 調用 Service 刪除圖片
            boolean result = sService.removeImage(productId, imageUrl);

            if (result) {
                return ResponseEntity.ok("圖片刪除成功！");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("圖片不存在或無法刪除");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("商品 ID 無效：" + productIdStr);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除圖片時發生錯誤：" + e.getMessage());
        }
    }





}
