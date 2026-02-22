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
