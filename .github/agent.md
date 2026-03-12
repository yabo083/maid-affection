# Touhou Maid: Affection — 开发工作流规范（双分支）

> 本文档是 AI 编码 Agent 在本项目中的行为规范。
> 本仓库同时维护 NeoForge 1.21.1 与 Forge 1.20.1，两条线必须隔离开发、独立构建、独立发布。

---

## 📐 项目基本信息

| 项 | main 分支 | forge-1.20.1 分支 |
|---|---|---|
| **分支定位** | 主线开发 | 降级兼容线 |
| **加载器** | NeoForge 21.1.x | Forge 47.4.x |
| **Minecraft** | 1.21.1 | 1.20.1 |
| **Java** | 21 | 17 |
| **核心依赖** | TouhouLittleMaid `1.5.0-neoforge+mc1.21.1` | TouhouLittleMaid `1.5.0-forge+mc1.20.1` |
| **Mod ID** | `touhou_maid_affection` | `touhou_maid_affection` |
| **许可证** | MIT | MIT |

---

## 🔀 分支与工作区约束

1. **严禁在 `main` 直接做 1.20.1/Forge 改动**。
2. Forge 迁移与维护仅在 `forge-1.20.1` 分支进行。
3. 建议使用 `git worktree` 并行开发，避免频繁切分支导致 IDE/Gradle 大量重建。
4. 功能同步策略：
   - `main` 新功能按需 **backport** 到 `forge-1.20.1`
   - `forge-1.20.1` bugfix 按需 **forward-port** 到 `main`
5. 不做单分支双加载器混编；差异通过分支隔离，不通过条件编译硬拼。

---

## 🔄 开发全流程

### Phase 0：需求与设计

1. 明确目标版本线（NeoForge 1.21.1 或 Forge 1.20.1）。
2. 调研 API 可用性：TouhouLittleMaid、Forge/NeoForge 文档、目标兼容模组。
3. 输出计划：列出受影响模块（构建、元数据、网络、注册、事件、CI）。

### Phase 1：编码实现

4. 先读后改，最小改动，不做无关重构。
5. **网络规范按分支区分**：
   - NeoForge：`CustomPacketPayload` + `PayloadRegistrar`
   - Forge：`SimpleChannel` + `registerMessage`
6. **配置规范按分支区分**：
   - NeoForge：`ModConfigSpec`
   - Forge：`ForgeConfigSpec`
7. 兼容性规范：
   - 软检测其他模组：`ModList.get().isLoaded("mod_id")`
   - 不引入运行时硬依赖（除非明确声明为必需依赖）
8. Mixin 规范：
   - 放在 `com.github.touhoumaidaffection.mixin`
   - 目标为 mod 类时 `remap = false`

### Phase 2：构建验证

9. 每次改动后必须构建：`./gradlew.bat build`。
10. 检查产物：`build/libs/*.jar` 是否存在且版本号正确。
11. 构建失败必须先读完整错误再修复，禁止盲试。

### Phase 3：版本管理与发布

12. 更新 `gradle.properties` 的 `mod_version`。
13. 版本/Tag 命名：
   - NeoForge（main）：`vX.Y.Z`
   - Forge（forge-1.20.1）：`vX.Y.Z-forge1.20.1`
14. 推送分支与 tag 后检查 Actions 与 Modrinth 发布是否成功。

---

## 🧱 元数据与资源约束

- `main`：`META-INF/neoforge.mods.toml`，`pack_format` 按 1.21.1。
- `forge-1.20.1`：`META-INF/mods.toml`，`pack_format = 15`。
- 两分支都必须保证：
  - 模组显示名、描述、许可证、依赖声明一致（除 loader/version 差异）。
  - 资源路径和语言键不随迁移破坏。

---

## 🤖 CI/CD 约束（双线独立）

1. CI 必须同时支持 `main` 与 `forge-1.20.1`。
2. Java 版本与分支绑定：
   - `main` -> Java 21
   - `forge-1.20.1` -> Java 17
3. 发布产物需可区分版本线（文件名或 tag）。
4. Modrinth 发布 loader 必须与分支一致：
   - `main` -> `neoforge`
   - `forge-1.20.1` -> `forge`

---

## ⚠️ 常见坑

1. 在错误分支改动导致 API/构建体系混杂。
2. Forge 分支遗留 `net.neoforged.*` 导包。
3. NeoForge 分支误改为 FG6/Java17。
4. `mods.toml` 与 `neoforge.mods.toml` 使用混淆。
5. tag 未区分版本线，导致发布覆盖或用户混淆。

---

## 📚 参考链接

- MinecraftForge 1.20.1: https://github.com/MinecraftForge/MinecraftForge/tree/1.20.1
- TouhouLittleMaid: https://github.com/TartaricAcid/TouhouLittleMaid
- Forge 文档（SimpleImpl）: https://docs.minecraftforge.net/en/1.20.x/networking/simpleimpl/
- NeoForge 文档: https://docs.neoforged.net/
