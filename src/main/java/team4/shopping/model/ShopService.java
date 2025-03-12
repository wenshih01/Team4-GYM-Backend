package team4.shopping.model;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShopService {

    @Autowired
    private ShopRepository sRepo;

    // 新增商品
    public GymBean saveGym(GymBean gym) {
        return sRepo.save(gym);
    }
    
    
    
    

    // 刪除商品
    public boolean deleteGymById(Integer id) {
        if (sRepo.existsById(id)) {
            sRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // 查詢單筆商品
    public Optional<GymBean> findGymById(Integer id) {
        return sRepo.findById(id);
    }

    // 查詢所有商品
    public List<GymBean> findAllGyms() {
        return sRepo.findAll();
    }

    // 分頁查詢商品
    public Page<GymBean> findAllByPage(Pageable pageable) {
        return sRepo.findAll(pageable);
    }

    // 根據名稱模糊查詢商品
    public List<GymBean> findGymsByName(String name) {
        return sRepo.findByNameContaining(name);
    }

    // 查詢上架或下架商品
    public List<GymBean> findProductsByStatus(int status) {
        return sRepo.findByStatus(status);
    }

    // 查詢特定分類的商品
    public List<GymBean> findGymsByCategory(String category) {
        return sRepo.findByCategory(category);
    }

    // 查詢特定分類且上架的商品
    public List<GymBean> findActiveGymsByCategory(String category) {
        return sRepo.findByCategoryAndStatus(category, 1); // 1 表示上架
    }

    // 查詢庫存不足的商品
    public List<GymBean> findLowStockGyms(int threshold) {
        return sRepo.findByStockQuantityLessThan(threshold);
    }

 // 更新商品資料，支持多張圖片
    public GymBean updateGym(GymBean gym, List<String> imageUrls) {
        // 確保商品存在
        Optional<GymBean> optionalGym = sRepo.findById(gym.getProductId());
        if (optionalGym.isEmpty()) {
            throw new IllegalArgumentException("商品不存在，無法更新！");
        }

        GymBean existingGym = optionalGym.get();

        // 更新商品基本資訊
        updateGymDetails(existingGym, gym);

        // 清除舊圖片並新增新圖片
        if (imageUrls != null && !imageUrls.isEmpty()) {
            clearAndAddImages(existingGym, imageUrls);
        } else {
            System.out.println("未提供新圖片，保留原有圖片。");
        }

        // 保存更新後的商品資料
        try {
            return sRepo.save(existingGym);
        } catch (Exception e) {
            throw new RuntimeException("商品更新失敗：" + e.getMessage());
        }
    }

    private void updateGymDetails(GymBean existingGym, GymBean updatedGym) {
        existingGym.setName(updatedGym.getName());
        existingGym.setDescription(updatedGym.getDescription());
        existingGym.setPrice(updatedGym.getPrice());
        existingGym.setStockQuantity(updatedGym.getStockQuantity());
        existingGym.setCategory(updatedGym.getCategory());
        existingGym.setStatus(updatedGym.getStatus());
        existingGym.setUpdatedAt(LocalDateTime.now());
    }

    private void clearAndAddImages(GymBean existingGym, List<String> imageUrls) {
       
        // 新增新圖片
        for (String imageUrl : imageUrls) {
            ProductsImage newImage = new ProductsImage();
            newImage.setImageUrl(imageUrl);
            newImage.setGym(existingGym);
            existingGym.getImages().add(newImage);
        }
    }





    // 根據商品 ID 更新商品狀態（0=下架, 1=上架, 2=假刪除）
    public GymBean updateProductStatus(int productId, int status) {
        Optional<GymBean> gymOpt = sRepo.findById(productId);
        if (gymOpt.isPresent()) {
            GymBean gym = gymOpt.get();
            gym.setStatus(status); // 設置新狀態
            gym.setUpdatedAt(java.time.LocalDateTime.now()); // 更新最後修改時間
            return sRepo.save(gym);
        }
        return null;
    }

    // 切換商品上架/下架狀態
    public GymBean toggleProductStatus(int productId) {
        Optional<GymBean> gymOpt = sRepo.findById(productId);
        if (gymOpt.isPresent()) {
            GymBean gym = gymOpt.get();
            gym.setStatus(gym.getStatus() == 1 ? 0 : 1); // 切換狀態
            return sRepo.save(gym);
        }
        throw new IllegalArgumentException("商品未找到 ID: " + productId);
    }
    
    public List<String> getAllCategories() {
        return sRepo.findAllCategories(); // 假設有一個 repository 方法來獲取分類
    }


    //刪除圖片
    public boolean removeImage(int productId, String imageUrl) {
        // 查詢商品
        Optional<GymBean> optionalGym = sRepo.findById(productId);
        if (optionalGym.isEmpty()) {
            System.out.println("商品不存在，無法刪除圖片");
            return false;
        }

        GymBean gym = optionalGym.get();

        // 確認圖片是否存在於該商品中
        ProductsImage imageToRemove = gym.getImages().stream()
                .filter(img -> img.getImageUrl().equals(imageUrl))
                .findFirst()
                .orElse(null);

        if (imageToRemove == null) {
            System.out.println("圖片不存在於商品中，無法刪除：" + imageUrl);
            return false;
        }

        // 移除圖片記錄
        gym.getImages().remove(imageToRemove);

        try {
            // 保存更新後的商品數據
            sRepo.save(gym);

            // 刪除圖片文件
            boolean fileDeleted = deleteImageFile(imageUrl);
            if (!fileDeleted) {
                System.out.println("圖片記錄已刪除，但文件不存在或刪除失敗");
            }

            return true;
        } catch (Exception e) {
            System.out.println("刪除圖片時發生錯誤：" + e.getMessage());
            return false;
        }
    }

    /**
     * 刪除圖片文件
     *
     * @param imageUrl 圖片的 URL
     * @return 是否成功刪除文件
     */
    private boolean deleteImageFile(String imageUrl) {
        try {
            // 圖片的物理路徑
            String filePath = "C:/temp/upload" + imageUrl.replace("/shop/uploads/", "");
            System.out.println("嘗試刪除圖片文件：" + filePath);

            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            } else {
                System.out.println("圖片文件不存在：" + filePath);
                return false;
            }
        } catch (Exception e) {
            System.out.println("刪除圖片文件時發生錯誤：" + e.getMessage());
            return false;
        }
    }


}
