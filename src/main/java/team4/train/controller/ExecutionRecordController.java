package team4.train.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team4.train.model.ExecutionRecord;
import team4.train.service.ExecutionRecordDTO;
import team4.train.service.ExecutionRecordService;

import java.util.List;

@RestController
@RequestMapping("/api/executionRecords")
public class ExecutionRecordController {

    @Autowired
    private ExecutionRecordService executionRecordService;

    // 查詢所有執行記錄
    @GetMapping
    public List<ExecutionRecordDTO> getAllExecutionRecords() {
        return executionRecordService.getAllExecutionRecords();
    }

    // 根據 ID 查詢單一執行記錄
    @GetMapping("/{id}")
    public ResponseEntity<ExecutionRecordDTO> getExecutionRecordById(@PathVariable int id) {
        return executionRecordService.getExecutionRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 根據會員名稱模糊查詢
    @GetMapping("/searchByMemberName")
    public List<ExecutionRecordDTO> searchExecutionRecordsByMemberName(@RequestParam String memberName) {
        return executionRecordService.searchExecutionRecordsByMemberName(memberName);
    }

    // 根據會員 ID 查詢執行記錄
    @GetMapping("/searchByMemberId")
    public List<ExecutionRecordDTO> getExecutionRecordsByMemberId(@RequestParam int memberId) {
        return executionRecordService.getExecutionRecordsByMemberId(memberId);
    }
    
    // 刪除執行記錄
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExecutionRecord(@PathVariable int id) {
        executionRecordService.deleteExecutionRecord(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping
    public ExecutionRecordDTO createExecutionRecord(@RequestBody ExecutionRecordDTO dto) {
        // 呼叫 Service 來新增 ExecutionRecord
        return executionRecordService.createExecutionRecord(dto);
    }
}
