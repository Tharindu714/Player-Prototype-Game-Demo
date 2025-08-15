# 🎮 Player Prototype Game — Prototype Lab

<p align="center">
  <img width="1366" height="728" alt="image" src="https://github.com/user-attachments/assets/74075387-f391-403b-8308-c04d48e7fe7b" />
</p>

**Repository:** `https://github.com/Tharindu714/Player-Prototype-Game-Demo.git`

> A colourful Java Swing demo that demonstrates the **Prototype** design pattern. Create an original player, clone it multiple times to experiment with ‘what-if’ scenarios, and manipulate clones independently without affecting the original.

---

## ✨ Highlights

* 🔁 **Prototype Pattern**: create deep clones of `Player` objects via a copy-constructor / `clonePrototype()` method.
* 🧪 Experiment safely: original kept intact (index 0) while clones are independent instances.
* 🎛️ Interactive GUI: clone single/multiple players, apply damage/heal/XP/level-up, edit names, remove clones.
* 🛡️ **Anti-patterns considered**: avoid `Object.clone()` pitfalls, deep-copy via copy-constructor, avoid shared mutable fields, keep cloning logic explicit and safe.

---

## 🚀 Features

* Create new clones of the original or any selected player.
* Mass cloning for stress-testing scenarios (create dozens/hundreds of clones).
* Apply actions to selected player: Take Damage, Heal, Gain XP, Level Up.
* Double-click a player to rename.
* Original player is always preserved at index `00` — clones live after it.

---

## 🛠️ Build & Run

Requires **Java 8+**.

```bash
# from repo root
javac PlayerPrototypeGame.java
java PlayerPrototypeGame
```

> The app launches a colourful Player Prototype Lab window. The original player is created by default (Hero). Use the left controls to clone and the right panel to manage players.

---

## 🧭 Design Overview

**Core components**

* `Player` — prototype object holding state: `name`, `health`, `experience`, `level`. Implements a safe cloning method via copy-constructor and `clonePrototype()`.
* `PlayerManager` — holds the original and clones, keeps original at index 0, provides utility operations (addClone, removeAt, clearClonesKeepOriginal).
* `GameFrame` — Swing UI that presents the original player card, cloning controls, player list and action buttons.
* `HeaderPanel` — attractive gradient header for theme consistency.

**Why Prototype?**

* Quickly create many independent copies of an object without going through construction and initialization code each time.
* Useful for simulations, testing strategies, or populating scenarios with slight variations.

---

## ✅ Anti-patterns avoided (rules applied)

* ❌ \*\*Avoid ****`Cloneable`**** + \*\***`Object.clone()`** — used copy-constructor to ensure explicit deep-copy behavior.
* ✅ **No shallow copies of mutable fields** — add deep copy logic if you later include collections or nested mutable objects.
* ✅ **No global mutable state** — manager holds instances; UI manipulates selected instances only.
* ⚠️ **Persistence & concurrency** — demo is single-process Swing; for multi-threaded or persistent systems, add synchronization and storage policies.

---

## 📐 UML (PlantUML)

Paste into [https://www.plantuml.com/plantuml](https://www.plantuml.com/plantuml) to render diagrams.

### Class Diagram

<p align="center">
  <img width="567" height="641" alt="UML-Light" src="https://github.com/user-attachments/assets/0a656bc8-6ae7-4392-915f-e4e3605d3180" />
</p>

### Sequence Diagram (Clone & Modify)

<p align="center">
  <img width="751" height="422" alt="Seq-Light" src="https://github.com/user-attachments/assets/492badaa-cbe1-4f91-836a-0d8f9937838f" />
</p>

---

## 📸 Scenario Photo

![Scenario 10](https://github.com/user-attachments/assets/df1537a7-9203-4c9b-9f60-65994f512ea1)

---

## 🧪 Example Usage

1. Launch the app. The original player `Hero` appears at index `00`.
2. Click **Clone Selected / Original** to create a clone.
3. Select a clone, click **Take Damage -10**, and observe the clone’s HP change while the original remains unchanged.
4. Use **Mass Clone** to quickly create multiple copies for stress-testing.

---

## 🔧 Extensions & Notes

* Add randomized variation when cloning (e.g., ±5 HP) to simulate diverse populations.
* Persist player sets to disk (JSON) to save scenarios.
* For heavy object graphs, ensure deep-copy semantics (copy nested collections / objects) in the copy-constructor.

---

## 📮 Contribution

Fork, add features (persistence, randomization, scenario export), and send PRs. Please include tests for cloning correctness (cloned instances should not share mutable internal references unless intended).

---

## 📝 License

MIT — reuse and adapt freely.

---

Made with ❤️ — Tharindu's Player Prototype Game Demo. Happy experimenting! ⚔️

