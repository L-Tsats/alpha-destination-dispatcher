# Briefing Diagrams

Two finalized visuals for the group briefing. Double-click an `.svg` to open it
in your browser, or open in VS Code and use the preview (right-click → Open
Preview) to view it rendered.

1. **1-system-architecture.svg** — the whole system: the 4 modules (keypad,
   central brain, elevator arm, evaluation) + optional visualizer, with the REST
   message flows between them. Use this to walk the team through "what the
   system is."

2. **2-build-plan.svg** — the two-stage plan: Stage 1 (4 cars / 40 floors, one
   section, shippable) → Stage 2 (20 / 200, tiled sections + transfers), plus
   the real cable-weight constraint that motivates the split. Use this to show
   "how we build it safely."

Export to PNG for slides: open the SVG in a browser and screenshot, or use any
SVG→PNG tool. Colors are consistent across both (teal = elevator / build-now,
purple = brain / advanced, orange = keypad, pink = evaluation).
