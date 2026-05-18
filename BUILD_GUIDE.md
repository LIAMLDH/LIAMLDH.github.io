# 粉笔题库采集器 - 构建指南

## 项目概述

这是一个Android应用，使用无障碍服务（AccessibilityService）自动采集粉笔App中的题目数据。

### 核心功能

1. **无障碍服务自动采集**：启动服务后，在粉笔App中刷题时自动捕获屏幕上的题目、选项、答案和解析
2. **本地题库存储**：使用Room数据库存储采集的题目
3. **去重功能**：基于题目内容生成MD5哈希值自动去重
4. **多格式导出**：支持导出为JSON和TXT格式
5. **后台服务**：前台服务确保在后台持续运行

## 项目结构

```
/workspace/
├── app/
│   ├── build.gradle.kts              # App模块构建配置
│   ├── proguard-rules.pro            # ProGuard规则
│   └── src/main/
│       ├── AndroidManifest.xml       # 应用清单文件
│       ├── java/com/fenbi/collector/
│       │   ├── FenbiCollectorApp.kt      # Application类
│       │   ├── data/
│       │   │   ├── Question.kt          # 题目实体类
│       │   │   ├── QuestionDao.kt        # Room DAO
│       │   │   ├── AppDatabase.kt       # Room数据库
│       │   │   └── QuestionExporter.kt  # 导出功能
│       │   ├── service/
│       │   │   ├── QuestionCaptureService.kt  # 无障碍服务
│       │   │   └── CaptureForegroundService.kt # 前台服务
│       │   └── ui/
│       │       ├── MainActivity.kt      # 主界面
│       │       └── MainViewModel.kt     # ViewModel
│       └── res/
│           ├── xml/
│           │   ├── accessibility_service_config.xml  # 无障碍服务配置
│           │   ├── file_paths.xml        # FileProvider配置
│           │   └── ...
│           └── values/
│               ├── strings.xml          # 字符串资源
│               └── ...
├── build.gradle.kts                # 根项目构建配置
├── settings.gradle.kts             # Gradle设置
└── gradle.properties              # Gradle属性
```

## 技术栈

- **语言**：Kotlin
- **UI框架**：Jetpack Compose
- **架构**：MVVM
- **数据库**：Room
- **异步**：Coroutines + Flow
- **依赖注入**：手动（简单项目）
- **最低SDK**：26 (Android 8.0)
- **目标SDK**：34 (Android 14)

## 构建要求

### 环境要求

- **JDK 17 或 21**（推荐JDK 17）
- **Android SDK 34**
- **Gradle 8.x**
- **Android Gradle Plugin 8.7.3**

### 不支持的Java版本

- Java 25（当前环境版本，不兼容）

### 设置Java 17环境

#### macOS (使用Homebrew)

```bash
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

#### Linux (使用apt)

```bash
sudo apt-get install openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

#### Windows

下载并安装 [Adoptium JDK 17](https://adoptium.net/)

## 构建步骤

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd /workspace
```

### 2. 配置Java环境

```bash
export JAVA_HOME=/path/to/jdk17
export PATH=$JAVA_HOME/bin:$PATH
java -version  # 确认显示17.x.x
```

### 3. 构建Debug APK

```bash
./gradlew assembleDebug
```

### 4. 构建Release APK

```bash
./gradlew assembleRelease
```

### 5. 安装到设备

```bash
./gradlew installDebug
```

## 使用说明

### 首次使用

1. 安装APK到Android设备
2. 打开应用
3. 点击"前往设置"按钮
4. 在系统设置中开启"粉笔题库采集"无障碍服务
5. 返回应用，点击开关启动服务

### 日常使用

1. 打开粉笔App开始刷题
2. 应用在后台自动采集题目
3. 可随时打开应用查看已采集的题目
4. 点击"导出"按钮分享题库

## 核心代码说明

### AccessibilityService实现

服务通过`QuestionCaptureService`实现，关键点：

1. **屏幕内容读取**：使用`rootInActiveWindow`获取当前屏幕内容
2. **题目解析**：`QuestionParser`类负责从屏幕文本中提取：
   - 题目文本
   - 选项列表
   - 用户答案
   - 正确答案
   - 解析内容
3. **自动保存**：检测到新题目后自动保存到Room数据库
4. **去重**：基于题目内容哈希值避免重复保存

### 前台服务

使用`CaptureForegroundService`确保应用在后台持续运行，显示常驻通知。

## 注意事项

1. **权限要求**：
   - 无障碍服务权限
   - 通知权限（Android 13+）
   - 存储权限（Android 10及以下）

2. **兼容性问题**：
   - 仅支持粉笔App的特定页面结构
   - 可能需要根据粉笔App更新调整解析逻辑

3. **性能考虑**：
   - 服务持续运行会消耗少量电量
   - 大量题目可能占用较多存储空间

## 故障排除

### 服务无法启动

1. 确认已在系统设置中开启无障碍服务
2. 检查通知权限是否授予
3. 查看应用日志排查问题

### 题目采集不完整

1. 确保在题目详情页面（显示完整题目、答案、解析）
2. 粉笔App界面更新后可能需要更新解析逻辑

### 构建失败

1. 确认Java版本为17或21
2. 确认Android SDK已安装且配置正确
3. 检查Gradle版本兼容性
4. 清理缓存：`./gradlew clean`

## 未来改进方向

1. 支持更多题库App
2. 添加云端同步功能
3. 智能识别更多题型
4. 支持错题本功能
5. 添加统计报表

## License

MIT License
