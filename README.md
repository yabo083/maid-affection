<p align="center">
  <img src="image/README/1773209564540.png" alt="Kiss your maid!" width="600"/>
</p>

<h1 align="center">💋 Touhou Maid: Affection</h1>

<p align="center">
  <b>Kiss your Touhou Little Maid. Because she deserves it.</b>
</p>

<p align="center">
  <a href="README_zh.md">🌏 中文</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.1-green?style=flat-square" alt="MC 1.21.1"/>
  <img src="https://img.shields.io/badge/NeoForge-21.1.x-orange?style=flat-square" alt="NeoForge"/>
  <img src="https://img.shields.io/badge/Requires-Touhou_Little_Maid_1.5.0+-blue?style=flat-square" alt="TLM"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="MIT"/>
</p>

---

## ✨ Features

| Feature | Description |
|---|---|
| 💋 **Kiss Interaction** | Sneak + empty hand + right-click your maid to kiss her |
| 💕 **Heart Particles** | Romantic heart particles spawn between you and your maid |
| 🔊 **Kiss Sound Effects** | Plays crisp kissing sounds (7 random variants) |
| 📈 **Favorability Boost** | Each kiss grants **+3 favorability** (30s cooldown) |
| 👀 **Maid Gaze** | Your maid turns to look at you during the kiss |
| ⏱️ **Tiered Cooldown** | Cooldown decreases as maid favorability rises: 5s → 3s → 1s → 0s |
| 🙏 **Maid's Prayer Buff** | Kiss 3 times within 10s to grant Regeneration I (30s) to both you and your maid |
| ⚙️ **Fully Configurable** | All values tunable via `config/touhou_maid_affection-common.toml` |
| 📦 **CarryOn Compatible** | Auto-detects CarryOn mod and avoids interaction conflicts |
| 🧲 **Offhand Attraction** | Holding temptation items (e.g. cake) in your offhand also attracts maids |

## 🎮 How to Trigger

### Default (without CarryOn)

```
Sneak (Shift) + Empty Hand + Right-click your maid
```

### With CarryOn installed

Since CarryOn uses "Sneak + Both Hands Empty + Right-click" to pick up entities, this mod automatically adjusts to avoid conflicts:

```
Sneak (Shift) + Main Hand Empty + Offhand Holding Any Item + Right-click your maid
```

> 💡 **Tip**: Just put a torch, flower, or anything in your offhand. With both hands empty, CarryOn takes over (picks up the maid).

## 🧲 Offhand Maid Attraction

In vanilla TouhouLittleMaid, only **main hand** cake attracts maids. This mod extends that behavior via Mixin:

```
Offhand holding temptation item (default: cake) → Maid is also attracted
```

> 🎀 **Best combo with CarryOn**: Hold cake in offhand to attract your maid, then sneak + right-click to kiss — seamless!

## 📥 Installation

1. Install **Minecraft 1.21.1** + **NeoForge 21.1.x**
2. Install **[Touhou Little Maid](https://modrinth.com/mod/touhou-little-maid)** 1.5.0+
3. Drop `maid-affection-x.x.x.jar` into your `.minecraft/mods/` folder
4. Launch the game!

## 🛠️ Build from Source

```bash
git clone https://github.com/yabo083/maid-affection.git
cd maid-affection
./gradlew build
```

Output jar at `build/libs/maid-affection-x.x.x.jar`.

## 📋 Technical Details

- **Mod ID**: `touhou_maid_affection`
- **API**: Uses TouhouLittleMaid's `InteractMaidEvent` event API
- **Networking**: Custom `KissMaidPayload` packet (Server → Client) for particle sync
- **Compatibility**: Soft-detects CarryOn via `ModList.isLoaded()`, zero hard dependencies
- **Favorability**: Uses TLM's built-in `FavorabilityManager` + custom `Type("Kiss", 3, 600)`

## 📄 License

[MIT License](LICENSE) — Free to use, modify, and distribute.

---

<p align="center">
  <i>Made with ❤️ for the Touhou Little Maid community</i>
</p>
