## Open In Inventory 2.0.0 -> 2.0.1

Fix template support in config

---

## Open In Inventory 1.2.1 -> 2.0.0

Breaking Changes:
- The format of `enabled_items` has been changed to: `ItemID` or `{ stack: ItemStack, sneak?: bool }`

New Features:
- `add` command, with almost everything
  - by default, it will add your main hand item to config. Use `--hotbar` to all valid stacks in your hotbar at once
  - `--wildcard` or `-w` can be used for ignoring additional data, e.g. NBT
  - `--sneak` or `-s` to set `sneak?: bool` to true
  - `--show` to only display collected items, without adding them to config
- Message for `refresh` command
- better output for replace template command

---
