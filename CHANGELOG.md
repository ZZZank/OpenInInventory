# Open In Inventory 1.0.0 -> 1.1.0

- Fix redundant Java 21 requirement on Minecraft versions that doesn't actually require Java 21
- Allow template to be usd in custom support config
    - Example: `example:{color}_bag` means add support for `example:white_bag`, `example:black_bag` and so on
    - Builtin templates: `color` and `armor`, you can use `/open-in-inventory replaceTemplate ...` to view replace result of the template
    - You can also register your own template via KubeJS or CrT
- Try to fix CraftTweaker support

---
