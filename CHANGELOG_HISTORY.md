# Open In Inventory 1.2.0 -> 1.2.1

- Fix used template in builtin ae2 compat

# Open In Inventory 1.1.1 -> 1.2.0

- Fix tooltip not disabled when sneak
- better ScreenClosedEvent impl
  - should fix the short interval (1 tick?) between screen set to null and this event invoked
- New template variant: Immediate Template

Immediate Template is `{a|b|c|...}` style template that does not require registration to use. Each replacement string is
split by `|`, and surrounded by `{` and `}`. For example:
- `{iron|gold|diamond}_backpack` -> `["iron_backpack", "gold_backpack", "diamond_backpack"]`
- `{a|b|}` -> `["a", "b", ""]`

---

# Open In Inventory 1.1.0 -> 1.1.1

- Add builtin support for Crafting On A Stick

---

# Open In Inventory 1.0.0 -> 1.1.0

- Fix redundant Java 21 requirement on Minecraft versions that doesn't actually require Java 21
- Allow template to be usd in custom support config
    - Example: `example:{color}_bag` means add support for `example:white_bag`, `example:black_bag` and so on
    - Builtin templates: `color` and `armor`, you can use `/open-in-inventory replaceTemplate ...` to view replace result of the template
    - You can also register your own template via KubeJS or CrT
- Try to fix CraftTweaker support

---
