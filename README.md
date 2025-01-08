
# 🌟 DeepSeek OCR

## 📖 项目简介

DeepSeek OCR 是一个基于Deepseek AI模型的智能文字识别系统，旨在通过图像识别技术提取图像中的文本信息。该项目使用了 DeepSeek API 进行 OCR 处理，支持多种上传方式，包括文件上传和 URL 上传。

## 🛠️ 功能介绍

- **📤 图像上传**: 支持通过拖拽、点击或粘贴方式上传图像文件。
- **🔍 OCR 识别**: 通过 DeepSeek API 进行图像中的文字识别。
- **📄 结果展示**: 显示识别结果，并提供复制功能。
- **🔑 认证设置**: 用户可以设置认证信息（Auth Token 和 Chat Session ID）以启用 OCR 功能。
- **📚 帮助文档**: 提供对认证参数的获取说明，帮助用户更好地使用该系统。

## 🚀 部署指南

### 🛠️ 环境要求

- JDK 8 或更高版本
- Maven 3.6 或更高版本
- Docker

### 🐳 Docker 一键部署（推荐）

1. **已推送到 Docker 仓库**

  已经将镜像推送到 Docker 仓库，可以直接使用以下命令进行一键部署：

   ```bash
   docker run -p 8088:8088 sexgirls/ocr-based-deepseek:latest
   ```

   访问 `http://localhost:8088` 来使用应用。
   
### 🖥️ 本地部署

1. **克隆项目**

   ```bash
   git clone https://github.com/yourusername/ocr-based-deepseek.git
   cd ocr-based-deepseek
   ```

2. **构建项目**

   使用 Maven 构建项目：

   ```bash
   mvn clean package
   ```

3. **运行应用**

   运行 Spring Boot 应用：

   ```bash
   java -jar target/ocr-based-deepseek-0.0.1-SNAPSHOT.jar
   ```

   默认情况下，应用将在 `http://localhost:8088` 上运行。


## 🔑 参数获取说明

在使用 OCR 功能之前，用户需要设置以下认证参数：

1. **Auth Token**: 这是与 DeepSeek API 交互所需的认证令牌。可以在 DeepSeek 的开发者平台获取。
<img width="1074" alt="auth-token-guide" src="https://github.com/user-attachments/assets/5ccb81ad-4274-4af9-b779-ba50a51b4411" />


3. **Chat Session ID**: 这是用于标识会话的 ID，通常在进行多次请求时需要保持一致。可以在 DeepSeek 的 API 文档中找到相关信息。
<img width="1067" alt="session-id-guide" src="https://github.com/user-attachments/assets/d2900559-eb7e-418b-a1c9-55c87eeb0f41" />

### ⚙️ 设置认证信息

在应用的设置侧边栏中，用户可以输入上述参数并点击“保存认证信息”按钮。成功保存后，上传功能将被启用。

## 📝 使用说明

1. 打开应用后，用户可以选择上传图像文件或输入图像 URL。
2. 上传后，系统将自动进行 OCR 识别，并在结果区域展示识别结果。
3. 用户可以通过点击“复制内容”按钮将识别结果复制到剪贴板。

## 🤝 贡献

欢迎任何形式的贡献！如果您有建议或发现问题，请提交 issue 或 pull request。

## 📜 许可证

该项目采用 MIT 许可证，详细信息请查看 [LICENSE](LICENSE) 文件。

---

如需更多信息，请参考项目中的文档或联系项目维护者。

希望这些表情能让你的 README 文件更加生动有趣！如果有其他需求，请随时告诉我。
