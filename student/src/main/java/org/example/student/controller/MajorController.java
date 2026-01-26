package org.example.student.controller;

import org.example.student.dto.MajorDTO;
import org.example.student.entity.Major;
import org.example.student.service.StudentService;
import org.example.student.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/major")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176", "http://localhost:5177", "http://localhost:5178"})
public class MajorController {
    
    @Autowired
    private StudentService studentService;
    
    // 获取所有专业
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<List<MajorDTO>>> getAllMajors(HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (List<MajorDTO>) null));
        }
        
        try {
            List<Major> majors = studentService.getAllMajors();
            List<MajorDTO> majorDTOs = majors.stream()
                    .map(MajorDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Result.success("获取专业列表成功", majorDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取专业列表失败: " + e.getMessage()));
        }
    }
    
    // 根据ID获取专业
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ResponseEntity<Result<MajorDTO>> getMajorById(@PathVariable Long id, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (MajorDTO) null));
        }
        
        try {
            return studentService.getMajorById(id)
                    .map(major -> ResponseEntity.ok(Result.success("获取专业成功", new MajorDTO(major))))
                    .orElseGet(() -> ResponseEntity.badRequest()
                            .body(Result.error("专业不存在")));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("获取专业失败: " + e.getMessage()));
        }
    }
    
    // 创建或更新专业
    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<Result<MajorDTO>> saveMajor(@RequestBody Major major, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", (MajorDTO) null));
        }
        
        try {
            Major savedMajor = studentService.saveMajor(major);
            MajorDTO majorDTO = new MajorDTO(savedMajor);
            return ResponseEntity.ok(Result.success("保存专业成功", majorDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("保存专业失败: " + e.getMessage()));
        }
    }
    
    // 删除专业
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<Result<String>> deleteMajor(@PathVariable Long id, HttpServletRequest request) {
        // 处理预检请求
        if (request.getMethod().equals("OPTIONS")) {
            return ResponseEntity.ok(Result.success("OK", "Success"));
        }
        
        try {
            // 检查是否有学生属于这个专业
            if (studentService.hasStudentsInMajor(id)) {
                return ResponseEntity.badRequest()
                        .body(Result.error("有学生属于该专业，不能删除"));
            }
            
            studentService.deleteMajor(id);
            return ResponseEntity.ok(Result.success("删除专业成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error("删除专业失败: " + e.getMessage()));
        }
    }
}