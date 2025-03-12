package team4.train.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import team4.train.model.PlanDetail;
import team4.train.model.PlanDetailRepository;
import team4.train.model.TrainBean;
import team4.train.model.TrainBeanRepository;
import team4.train.model.TrainingPlanRepository;

@Service
public class TrainBeanService {

	@Autowired
	private TrainBeanRepository trainBeanRepository;

	@Autowired
	private PlanDetailRepository planDetailRepository;

	@Autowired
	private TrainingPlanRepository trainingPlanRepository;

	// ------------------------
	// 1. 新增健身動作 (允許 imageUrl 為 null)
	// ------------------------
	public TrainBean addAction(TrainBean trainBean) {
		// trainBean.getImageUrl() == null 也可正常存入 DB
		return trainBeanRepository.save(trainBean);
	}

	// ------------------------
	// 2. 分頁查詢健身動作
	// ------------------------
	public Page<TrainBean> getAllActions(Pageable pageable) {
		return trainBeanRepository.findAll(pageable);
	}

	// ------------------------
	// 3. 模糊查詢 (依動作名稱)
	// ------------------------
	public Page<TrainBean> searchActions(String name, Pageable pageable) {
		return trainBeanRepository.findByNameContaining(name, pageable);
	}

	// ------------------------
	// 4. 根據 ID 查詢健身動作 (並載入 PlanDetail)
	// ------------------------
	@Transactional
	public TrainBean getActionById(int id) {
		TrainBean trainBean = trainBeanRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("TrainBean not found"));

		// 手動加載所有 PlanDetail 的關聯數據
		trainBean.getPlanDetails().forEach(detail -> {
			Hibernate.initialize(detail.getTrainingPlan());
		});

		return trainBean;
	}

	// ------------------------
	// 5. 更新健身動作
	// ------------------------
	@Transactional
	public TrainBean updateAction(int id, TrainBean trainBean) {
		TrainBean existingBean = trainBeanRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("TrainBean not found"));

		existingBean.setName(trainBean.getName());
		existingBean.setParts(trainBean.getParts());
		existingBean.setTeaching(trainBean.getTeaching());
		existingBean.setUrl(trainBean.getUrl());
		existingBean.setCalories(trainBean.getCalories());

		// 若前端有傳新的 imageUrl，則更新；若是 null 就維持原本值
		existingBean.setImageUrl(trainBean.getImageUrl());

		// 若有 PlanDetail 修改，則同步更新
		if (trainBean.getPlanDetails() != null) {
			for (PlanDetail detail : trainBean.getPlanDetails()) {
				if (detail.getId() != 0) {
					PlanDetail existingDetail = planDetailRepository.findById(detail.getId())
							.orElseThrow(() -> new RuntimeException("PlanDetail not found"));

					existingDetail.setSets(detail.getSets());
					existingDetail.setReps(detail.getReps());
					existingDetail.setCalories(detail.getCalories());

					// 手動加載 trainingPlan
					Hibernate.initialize(existingDetail.getTrainingPlan());
				}
			}
		}

		return trainBeanRepository.save(existingBean);
	}

	// ------------------------
	// 6. 刪除健身動作
	// ------------------------
	public void deleteAction(int id) {
		trainBeanRepository.deleteById(id);
	}

	// ------------------------
	// 7. 上傳圖片 (接收 MultipartFile, 存到本機, 更新 imageUrl)
	/**
     * 範例：上傳圖片，存到 "uploads/images/" 資料夾
     * 並將 imageUrl 設為 "/uploads/images/{檔名}"
     */
    @Transactional
    public TrainBean uploadImage(int id, MultipartFile file) {
        // 1. 找到要更新的 TrainBean
        TrainBean existingBean = trainBeanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TrainBean not found"));

        // 2. 若檔案為空，就不更新 imageUrl
        if (file == null || file.isEmpty()) {
            return existingBean;
        }

        // 3. 產生唯一檔名 (UUID + 副檔名)
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + ext;

        try {
            // 4. 構建要存放的目錄路徑 (uploads/images)
            Path folderPath = Paths.get("uploads", "images").toAbsolutePath().normalize();
            // 若目錄不存在，建立目錄
            Files.createDirectories(folderPath);

            // 5. 確定完整檔案路徑 (uploads/images/xxx.png)
            Path filePath = folderPath.resolve(newFileName);

            // 6. 將檔案內容複製到該路徑
            // （可使用 file.transferTo(...) 或 Files.copy(...)）
            file.transferTo(filePath.toFile());
            // 或者：
            // Files.copy(file.getInputStream(), filePath);

            // 7. 更新 Bean 的 imageUrl 為 "/uploads/images/xxx.png"
            // 讓前端用此路徑存取
            existingBean.setImageUrl("/uploads/images/" + newFileName);

            // 8. 儲存更新後的 Bean
            return trainBeanRepository.save(existingBean);

        } catch (IOException e) {
            throw new RuntimeException("儲存檔案失敗: " + e.getMessage(), e);
        }
    }
}
