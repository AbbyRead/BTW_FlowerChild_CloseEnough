# Better Than Wolves 1.0

*An In-Depth Overview of the Original Release*

**Better Than Wolves (BTW) 1.0** was the first public release of FlowerChild’s now-legendary Minecraft mod, targeting **Minecraft Beta 1.4_01**.

Very little formal documentation of this initial release survives. The original Minecraft Forum post has been lost to time, as later revisions overwrote early details, and the official changelogs do not explicitly describe what 1.0 introduced. What follows is a reconstruction based on:

* Surviving **[WIP](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/mods-discussion/1355587-wip-better-than-wolves-mod) forum posts**
* **Direct source-code analysis**
* **In-game testing**
* **NBT inspection**
* Community discussion and comparison with subsequent Minecraft updates

This document serves as both **historical record** and **design context** for the earliest incarnation of Better Than Wolves.

---

## Context: Minecraft Beta 1.4_01

At the time of BTW 1.0:

* No Creative Mode existed
* Redstone mechanics were primitive
* Minecarts lacked powered rails or detectors
* Automation was limited and often unreliable

BTW 1.0 emerged in this environment and immediately pushed Minecraft toward **mechanical realism**, **intentional friction**, and **player-driven engineering**—often years ahead of vanilla implementations.

---

## Core Design Themes Introduced

* **Redstone-controlled physical systems**
* **Automation with explicit cost and intent**
* **Mechanical realism over convenience**
* **Player responsibility for timing, power, and flow**

Many mechanics introduced here would later appear—sometimes almost verbatim—in vanilla Minecraft.

---

## Features Introduced in BTW 1.0

### Powered "Light Bulb" Block

A redstone-controlled light source.

* Appears as a glass block when unpowered
* Turns **bright yellow and opaque** when powered
* Can be activated via **direct or indirect redstone**
* One of the earliest examples of controllable lighting in Minecraft

**Significance:**
Predates redstone lamps and introduces the concept of *intentional illumination*.

---

### Hibachi (BBQ Block)

A redstone-activated fire source.

* Top surface ignites when powered
* Extinguishes automatically when power stops
* Top texture resembles **netherrack**
* Sides resemble **iron block**
* Fire exists only while powered—no permanent burning

**Significance:**
Introduces controlled fire as a mechanical system rather than a hazard.

---

### "Minecart Booster" Block

A precursor to powered rails.

* Placed **beneath minecart tracks**
* Requires **direct redstone power**
* **Powered:** accelerates minecarts
* **Unpowered:** slows or stops minecarts (acts as a brake)

Flavor text from FlowerChild:

> “If you apply a redstone current to the souls trapped in the sand, they might be ‘motivated’ to work for the player.”

Uses **Soul Sand**, framing automation as morally questionable but effective.

**Significance:**
Direct ancestor of powered rails, released *before* their vanilla introduction.

---

### "Minecart Pressure Plate" Block

A detector rail before detector rails existed.

* Detects passing minecarts
* Emits a redstone signal
* Cannot receive power itself
* Can trigger booster blocks and logic circuits

**Significance:**
Introduced reliable minecart detection years ahead of vanilla.

---

### Cement Bucket

A programmable construction material.

* Flows like lava or water
* Solidifies into **smooth stone** over time
* **If powered by redstone:** remains liquid indefinitely
* Can be used for:

  * Traps
  * Timed construction
  * Mass synchronized building
* Includes frozen and animated cement states internally

Early discussion even suggested future dyeable cement variants.

**Significance:**
Introduces **time, power, and state-based building mechanics**, something still rare even in modern Minecraft.

---

## Planned but Unreleased Concepts (from WIP)

### Photovoltaic Redstone Sensor

* Detects light level
* Outputs redstone signal
* Later realized as the **Detector Block**

### Focused Redstone Torch

* Projects a focused beam of light
* Intended for:
  * Displays
  * Signaling
  * Alignment
  * Tripwire-like logic
* Later evolved into the **Lens block**

## Internal Item & Block IDs (Observed)

|  ID | Item / Block               |
| --: | -------------------------- |
| 222 | Light Bulb (Unpowered)     |
| 223 | Light Bulb (Powered)       |
| 224 | Hibachi                    |
| 225 | Minecart Booster (Idle)    |
| 226 | Minecart Booster (Powered) |
| 228 | Active Cement              |
| 229 | Frozen Cement              |
| 478 | Bucket of Cement           |
