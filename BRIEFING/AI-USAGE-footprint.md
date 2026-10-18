# AI Usage — Cost & Environmental Footprint (as required by Spinellis)

> Honest, bounded accounting. Exact per-request provider figures are not
> published, so we report a grounded ESTIMATE with method + sources stated.

## Scope of what is attributed
An AI assistant was used throughout the DESIGN/planning discussion, but the only
parts that consumed meaningful *generative* resources (and are fair to attribute
to this project's footprint) are:
- the generated ARCHITECTURE/DESIGN DIAGRAMS (SVG images),
- the curation of the research reading list (fetching/summarising sources), and
- the bulk-writing of the design/spec MARKDOWN documents (the BRIEFING docs).

Usage was on a fixed **€40/month subscription** with a 2,000 monthly credit
allowance. The above generative work consumed about **65 of those 2,000 credits
(~3.25% of the month's allowance)** — roughly ~40 for the image generations,
~10 for the research-material curation, and ~15 for bulk-writing the markdown
docs. The rest of the collaboration was lightweight text that would fit a free
tier; we attribute only the ~65-credit generative portion.

## Monetary cost
- Fixed subscription: €40 / month, 2,000 credits.
- Attributable share: ~65 / 2,000 = **3.25%** of one month ->  **≈ €1.30**.
- Marginal cost beyond the already-paid subscription: effectively €0; the €1.30
  is the fair pro-rata share of the plan for this project's generative use.

## Environmental footprint (the ~65-credit generative portion)
Per-generation reference figures from published 2025 LLM-inference data:
- Text prompt (Google Gemini production median): ~0.24 Wh, ~0.03 g CO2e,
  ~0.26 mL water per prompt.
- Heavier / image-type generations run higher; "How Hungry is AI?" (arXiv
  2505.09598) reports up to tens of Wh for the most intensive generations.
(Content rephrased from sources for licensing compliance.)

Conservative estimate — treat the ~65 generative credits as heavier-than-text
work and use a high per-item figure of ~10 Wh each (deliberately rounded UP so we
do not undercount images):

  Energy:  65 * 10 Wh          = 650 Wh = **~0.65 kWh**
  Carbon:  0.65 kWh * ~0.2 kg/kWh (Greek grid, relatively clean)
                                = **~0.13 kg CO2e**  (range ~0.09-0.20 kg)
  Water:   650 Wh * ~1.1 mL/Wh = **~0.72 L**

### Footprint summary (attributed generative use)
- Monetary: **~€1.30** (3.25% pro-rata of a €40/month plan)
- Energy:   **~0.65 kWh**       (about a fridge running ~1-1.5 hours)
- Carbon:   **~0.13 kg CO2e**   (~a few hundred metres of car driving)
- Water:    **~0.72 L**         (a few glasses)

## Sources (acknowledged)
- Google 2025 sustainability disclosure (Gemini production per-prompt energy/
  water/CO2), reported via DataCenterDynamics.
- "How Hungry is AI? Benchmarking Energy, Water, and Carbon Footprint of LLM
  Inference", arXiv:2505.09598 (2025).

## One-line statement for the report
"An AI assistant was used for design, diagrams, and spec documents on a fixed
€40/month plan; the generative portion attributable to this project (~3.25% of
the monthly allowance) costs ≈€1.30 and is estimated at ~0.65 kWh, ~0.13 kg
CO2e, and ~0.72 L water, derived from published 2025 LLM-inference figures
(Google Gemini production data; 'How Hungry is AI?', arXiv:2505.09598)."
