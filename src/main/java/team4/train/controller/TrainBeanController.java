package team4.train.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import team4.train.model.TrainBean;
import team4.train.service.TrainBeanService;

@RestController
@RequestMapping("/api/actions")
//@CrossOrigin(origins = "*")
public class TrainBeanController {

    @Autowired
    private TrainBeanService trainBeanService;

    // ------------------------
    // 1. 新增健身動作 (允許 imageUrl 為 null)
    // ------------------------
    @PostMapping
    public TrainBean addAction(@RequestBody TrainBean trainBean) {
        // 前端若有傳 imageUrl 就存，若沒傳則會是 null
        return trainBeanService.addAction(trainBean);
    }

    // ------------------------
    // 2. 分頁查詢健身動作
    // ------------------------
    @GetMapping("/all")
    public Page<TrainBean> getAllActions(
       @PageableDefault(page = 0, size = 9999) Pageable pageable) {
        return trainBeanService.getAllActions(pageable);
    }

    // ------------------------
    // 3. 模糊查詢 (依動作名稱)
    // ------------------------
    @GetMapping("/search")
    public Page<TrainBean> searchActions(@RequestParam String name, Pageable pageable) {
        return trainBeanService.searchActions(name, pageable);
    }

    // ------------------------
    // 4. 根據 ID 查詢健身動作
    // ------------------------
    @GetMapping("/{id}")
    public ResponseEntity<TrainBean> getActionById(@PathVariable int id) {
        TrainBean trainBean = trainBeanService.getActionById(id);
        return ResponseEntity.ok(trainBean);
    }

    // ------------------------
    // 5. 更新健身動作 (允許 imageUrl 為 null)
    // ------------------------
    @PutMapping("/{id}")
    public ResponseEntity<TrainBean> updateAction(@PathVariable int id, @RequestBody TrainBean trainBean) {
        TrainBean updatedBean = trainBeanService.updateAction(id, trainBean);
        return ResponseEntity.ok(updatedBean);
    }
    
    // ------------------------
    // 6. 刪除健身動作
    // ------------------------
    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable int id) {
        trainBeanService.deleteAction(id);
    }

    // ------------------------
    // 7. 上傳圖片 (multipart/form-data)
    //    前端可用 <form> 或 axios 傳檔案
    // ------------------------
    @PostMapping("/uploadImage")
    public ResponseEntity<TrainBean> uploadImage(
            @RequestParam("id") int id,                       // 要更新的 TrainBean ID
            @RequestParam(value = "file", required = false) MultipartFile file  // 圖片檔案，可為 null
    ) {
        // 呼叫 service 方法進行檔案儲存與欄位更新
        TrainBean trainBean = trainBeanService.uploadImage(id, file);
        return ResponseEntity.ok(trainBean);
    }
}
