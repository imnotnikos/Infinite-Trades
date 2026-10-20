<p align="center"><img src="assets/social-preview.png" alt="Infinite Trades banner" width="100%"></p>

# Infinite Trades
Infinite Trades is a lightweight **Paper** plugin for 26.2 that makes villager trading painless without breaking vanilla reputation. Trades restock instantly and never run out, and prices no longer climb just because a trade gets used a lot.

<img src="assets/icon.png" width="250" height="250" alt="Infinite Trades icon" />

***
### Plugin made by imNikos.
 
[![Discord](https://img.shields.io/badge/Discord-Nikos'%20Wasteland-5865F2?style=flat-square&logo=discord&logoColor=white)](https://discord.gg/9xJgzNqEaJ)
[![Instagram](https://img.shields.io/badge/Instagram-imNikos_-E4405F?style=flat-square&logo=instagram&logoColor=white)](https://instagram.com/imnikos_)
[![YouTube](https://img.shields.io/badge/YouTube-imNikoss-FF0000?style=flat-square&logo=youtube&logoColor=white)](https://youtube.com/@imnikoss)
[![TikTok](https://img.shields.io/badge/TikTok-imNikos_-000000?style=flat-square&logo=tiktok&logoColor=white)](https://tiktok.com/@imnikos_)
[![GitHub](https://img.shields.io/badge/GitHub-imnotnikos-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/imnotnikos)
[![Modrinth](https://img.shields.io/badge/Modrinth-imNikos-1bd96a?style=flat-square&logo=modrinth&logoColor=white)](https://modrinth.com/user/imNikos)
[![CurseForge](https://img.shields.io/badge/CurseForge-imnotnikos-F16436?style=flat-square&logo=curseforge&logoColor=white)](https://www.curseforge.com/members/imnotnikos/projects)

***

## Features
**Infinite Stock**: Trades restock instantly and never lock with the red X.

**No Demand Pricing**: The vanilla "demand" price increase from trading a lot is removed.

**Vanilla Reputation**: Hitting a villager still raises its prices, and curing a zombie villager still gives the big discount.

**Wandering Traders**: Optionally give wandering traders infinite stock too.

**Per-World Control**: Turn the plugin off in any world from the config.

## Commands

/infinitetrades reload – Reloads config.yml.

/infinitetrades status – Shows the current settings.

_Available aliases:_ /itrades

## Permissions
infinitetrades.admin : Required to use the commands (default: op). Regular players don't need any permissions to trade.

## Configuration
```yaml
infinite-stock: true
remove-demand-pricing: true
include-wandering-traders: true
disabled-worlds: []
```

## Requirements
A Minecraft server running **Paper 26.2**.

Java 25.

# Installation
## For Server Owners
Navigate to the Releases page.

Download the latest InfiniteTrades jar.

Place the file into your server's /plugins/ directory.

Restart your server.

## Building
Requires JDK 25 and Maven.

    mvn package

Output: `target/InfiniteTrades-1.0.0.jar`
