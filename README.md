# 猫猫情侣判官 Cat Couple Judge

一款基于原生 Android Java 开发的情侣情感复盘工具。

用户在发生争执后，可以选择单人诉苦或双人裁判模式，填写事件经过与各自视角，由 AI 输出温柔、中立的复盘结果与改善建议。所有数据默认仅保存在本地 SQLite，不上传云端。

## 功能简介

- 4 位猫咪法官可选：小橘、布偶、奶牛、黑炭
- 支持单人诉苦与双人裁判两种模式
- 支持事件描述、视角填写与 AI 判决生成
- 判决结果按模块化卡片展示
- 支持存入错题本、本地历史记录查看
- 支持离线兜底判词

## 技术栈

- Android Studio Koala
- 原生 Android
- Java
- XML 布局
- SQLite
- OkHttp3

## 项目特点

- 纯本地存储，默认不上传用户隐私数据
- 原生 Java + XML 实现
- AI 输出采用固定结构，便于稳定展示与解析
- 界面风格为粉系、卡片式、Q 版猫咪主题

## 运行环境

- 最低 SDK：24
- 目标 SDK：34
- 建议使用较新的 Android Studio 与 Android SDK 环境

## 本地运行

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 配置 Android SDK
4. 运行到模拟器或真机
5. 如需使用在线 AI 判决，请在应用设置中填写 API Key

## 当前状态

当前项目已完成基础业务流程、页面结构、本地存储、AI 请求接入与错题本历史功能。

后续可继续完善：

- README 截图展示
- 应用图标优化
- 更多 AI 提供方支持
- 上架前的体验优化与测试

## 仓库地址

[GitHub Repository](https://github.com/EdwardCao86/couplejudge)
