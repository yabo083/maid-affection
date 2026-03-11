<p align="center">
  <img src="image/README/1773209564540.png" alt="Kiss your maid!" width="600"/>
</p>

<h1 align="center">💋 Maid Affection — 女仆亲亲</h1>

<p align="center">
  <b>Kiss your Touhou Little Maid. Because she deserves it.</b><br/>
  <i>亲亲你的女仆吧，她值得。</i>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.1-green?style=flat-square" alt="MC 1.21.1"/>
  <img src="https://img.shields.io/badge/NeoForge-21.1.x-orange?style=flat-square" alt="NeoForge"/>
  <img src="https://img.shields.io/badge/Requires-Touhou_Little_Maid_1.5.0+-blue?style=flat-square" alt="TLM"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="MIT"/>
</p>

---

## ✨ 功能特性

| 特性                     | 描述                                               |
| ------------------------ | -------------------------------------------------- |
| 💋**亲吻互动**     | 潜行 + 空手右击你的女仆，触发亲吻                  |
| 💕**爱心粒子**     | 亲吻时在两人之间生成浪漫的爱心粒子效果             |
| 🔊**亲吻音效**     | 播放清脆的亲吻音效                                 |
| 📈**好感度提升**   | 每次亲吻为女仆增加**+3 好感度**（30 秒冷却） |
| 👀**女仆凝视**     | 亲吻时女仆会转头看向你                             |
| ⏱️**防刷屏**     | 2 秒交互冷却，防止粒子刷屏                         |
| 📦**CarryOn 兼容** | 自动检测 CarryOn 模组，智能避免操作冲突            |

## 🎮 触发条件

### 默认（无 CarryOn）

```
潜行（Shift）+ 空手 + 右击你的女仆
```

### 安装了 CarryOn 时

由于 CarryOn 使用「潜行 + 双手空 + 右击」来搬起实体，本模组会自动调整触发方式以避免冲突：

```
潜行（Shift）+ 主手为空 + 副手持有任意物品 + 右击你的女仆
```

> 💡 **提示**：副手放一个火把、花或任何物品即可。双手全空时将由 CarryOn 接管（搬起女仆）。

## 📥 安装

1. 安装 **Minecraft 1.21.1** + **NeoForge 21.1.x**
2. 安装 **[Touhou Little Maid](https://modrinth.com/mod/touhou-little-maid)** 1.5.0+
3. 将 `maid-affection-x.x.x.jar` 放入 `.minecraft/mods/` 文件夹
4. 启动游戏！

## 🛠️ 从源码构建

```bash
git clone https://github.com/yabo083/maid-affection.git
cd maid-affection
./gradlew build
```

构建产物在 `build/libs/maid-affection-x.x.x.jar`。

## 📋 技术细节

- **Mod ID**: `maid_affection`
- **API**: 使用 TouhouLittleMaid 提供的 `InteractMaidEvent` 事件 API
- **网络**: 自定义 `KissMaidPayload` 包（Server → Client）用于同步粒子效果
- **兼容性**: 通过 `ModList.isLoaded()` 软检测 CarryOn，零硬依赖
- **好感度**: 使用 TLM 内置的 `FavorabilityManager` + 自定义 `Type("Kiss", 3, 600)`

## 📄 License

[MIT License](LICENSE) — 自由使用、修改和分发。

---

<p align="center">
  <i>Made with ❤️ for the Touhou Little Maid community</i>
</p>
