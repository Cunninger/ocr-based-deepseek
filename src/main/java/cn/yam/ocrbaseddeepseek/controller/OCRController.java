package cn.yam.ocrbaseddeepseek.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OCRController {
    private static final Log log = LogFactory.get();
    private static final String API_BASE_URL = "https://chat.deepseek.com/api/v0";

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/ocr")
    @ResponseBody
    public Map<String, Object> ocr(
            @RequestParam MultipartFile file,
            @RequestParam String authToken,
            @RequestParam String chatSessionId) {

        Map<String, Object> result = new HashMap<>();

        // 验证参数
        if (file == null || file.isEmpty()) {
            result.put("success", false);
            result.put("error", "请选择要上传的文件");
            return result;
        }

        if (!authToken.startsWith("Bearer ")) {
            result.put("success", false);
            result.put("error", "Auth Token格式不正确");
            return result;
        }

        try {
            // 1. 上传文件
            JSONObject uploadResponse = uploadFile(file, authToken);
            if (uploadResponse == null) {
                result.put("success", false);
                result.put("error", "文件上传失败或已达到速率限制");
                return result;
            }

            String fileId = uploadResponse.getJSONObject("data")
                    .getJSONObject("biz_data")
                    .getStr("id");

            waitForRateLimit();

            // 2. 获取文件信息
            JSONObject fetchResponse = fetchFile(fileId, authToken);
            if (fetchResponse == null) {
                result.put("success", false);
                result.put("error", "文件获取失败或已达到速率限制");
                return result;
            }

            waitForRateLimit();

            // 3. 进行OCR识别
            String ocrResult = processOcr(fileId, authToken, chatSessionId);
            if (ocrResult == null) {
                result.put("success", false);
                result.put("error", "OCR处理失败或已达到速率限制");
                return result;
            }

            result.put("success", true);
            result.put("result", ocrResult);
            return result;

        } catch (Exception e) {
            log.error("OCR处理错误", e);
            result.put("success", false);
            result.put("error", "处理过程中发生错误: " + e.getMessage());
            return result;
        }
    }

    private JSONObject uploadFile(MultipartFile file, String authToken) throws IOException {
        try {
            HttpResponse response = HttpRequest.post(API_BASE_URL + "/file/upload_file")
                    .header("Authorization", authToken)
                    .form("file", file.getBytes(), file.getOriginalFilename())
                    .execute();

            if (response.getStatus() == 429) {
                log.warn("已达到速率限制，等待中...");
                waitForRateLimit();
                return null;
            }

            if (response.getStatus() != 200) {
                log.error("上传失败，状态码: {}", response.getStatus());
                return null;
            }

            JSONObject responseData = JSONUtil.parseObj(response.body());
            if (!responseData.getInt("code", -1).equals(0) ||
                    !responseData.getJSONObject("data").getInt("biz_code", -1).equals(0)) {
                log.error("上传失败: {}", responseData);
                return null;
            }

            return responseData;
        } catch (Exception e) {
            log.error("上传错误", e);
            return null;
        }
    }

    private JSONObject fetchFile(String fileId, String authToken) {
        try {
            HttpResponse response = HttpRequest.get(API_BASE_URL + "/file/fetch_files")
                    .header("Authorization", authToken)
                    .form("file_ids", fileId)
                    .execute();

            if (response.getStatus() == 429) {
                log.warn("已达到速率限制，等待中...");
                waitForRateLimit();
                return null;
            }

            if (response.getStatus() != 200) {
                log.error("获取文件失败，状态码: {}", response.getStatus());
                return null;
            }

            return JSONUtil.parseObj(response.body());
        } catch (Exception e) {
            log.error("获取文件错误", e);
            return null;
        }
    }

    private String processOcr(String fileId, String authToken, String chatSessionId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("chat_session_id", chatSessionId);
            data.put("parent_message_id", 2);
            data.put("prompt", "请仅识别并返回图片中的文字内容，不要提供任何额外的解释、说明或信息。");
            data.put("ref_file_ids", new String[]{fileId});


            HttpResponse response = HttpRequest.post(API_BASE_URL + "/chat/completion")
                    .header("Authorization", authToken)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(data))
                    .execute();

            if (response.getStatus() == 429) {
                log.warn("已达到速率限制，等待中...");
                waitForRateLimit();
                return null;
            }

            if (response.getStatus() != 200) {
                log.error("OCR处理失败，状态码: {}", response.getStatus());
                return null;
            }

            StringBuilder result = new StringBuilder();
            String[] lines = response.body().split("\n");
            for (String line : lines) {
                if (line.startsWith("data: ")) {
                    try {
                        JSONObject jsonData = JSONUtil.parseObj(line.substring(6));
                        if (jsonData.containsKey("choices") && !jsonData.getJSONArray("choices").isEmpty()) {
                            JSONObject delta = jsonData.getJSONArray("choices").getJSONObject(0).getJSONObject("delta");
                            if (delta.containsKey("content")) {
                                result.append(delta.getStr("content"));
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            return result.toString();
        } catch (Exception e) {
            log.error("OCR处理错误", e);
            return null;
        }
    }

    private void waitForRateLimit() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}