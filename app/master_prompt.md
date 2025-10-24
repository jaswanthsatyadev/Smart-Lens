# 🔍 SMARTLENS - MASTER DEVELOPMENT PROMPT

**App Name:** SmartLens  
**Tagline:** "Scan Smarter, Choose Better"  
**Purpose:** Universal product scanner revealing health scores, ingredient safety, and better alternatives  
**Platform:** Android (Kotlin + Jetpack Compose + Material 3)  
**Target API:** Min SDK 24, Target SDK 35

***

## 📱 CORE CONCEPT

SmartLens scans any product (food, beauty, personal care) and instantly shows:
1. Health/Safety score (0-100) with color coding
2. Critical warnings (high sugar, harmful ingredients)
3. Better alternatives with improvement reasons
4. Where to buy (affiliate monetization)

**User Flow:** Scan → See Score → View Warnings → Check Alternatives → Buy Better Option

***

## 🎨 DESIGN SYSTEM

### **Color Palette**

**Score-Based Colors:**
- 80-100: Excellent Green `#4CAF50`
- 60-79: Good Light Green `#8BC34A`
- 40-59: Average Yellow `#FFC107`
- 20-39: Poor Orange `#FF9800`
- 0-19: Very Poor Red `#F44336`

**Category Accent Colors:**
- Food: Orange `#FF6B35`
- Beauty: Pink `#E91E63`
- Personal Care: Blue `#2196F3`

**UI Base:**
- Primary: Professional Blue `#1976D2`
- Background: Off-white `#FAFAFA`
- Surface: White `#FFFFFF`
- Text: Dark Gray `#212121`

### **Visual Hierarchy Principles**

**Above the Fold (No Scrolling Required):**
1. Large score circle (most important)
2. Critical warnings (if any)
3. Product name + image
4. Category badge

**Below the Fold (After Scrolling):**
5. Category-specific badges (Vegan, Nutri-Score, etc.)
6. Key metrics breakdown (3-5 most important nutrients/ingredients)
7. "Show Better Alternatives" CTA button
8. Full ingredients list (expandable/collapsible)
9. Where to buy section
10. Additional details (manufacturer, origin, etc.)

### **Component Style**

- Cards: 16dp corners, 2dp elevation, generous padding
- Progress bars: 8dp height, rounded, animated
- Buttons: 8dp corners, filled primary color
- Badges: Pill-shaped, 6dp padding, color-coded
- Typography: Bold for scores/names, Regular for content

***

## 🎯 PRODUCT CATEGORIES

### **1. Food & Beverages**
- Score based on: Sugar, salt, saturated fat, protein, fiber, processing level
- Display: Nutri-Score (A-E), NOVA (1-4), nutritional breakdown
- Alternatives: Lower sugar/salt, less processed, higher protein

### **2. Beauty & Cosmetics**
- Score based on: Harmful ingredients count, parabens, sulfates, allergens
- Display: Vegan/cruelty-free badges, ingredient safety warnings
- Alternatives: Paraben-free, sulfate-free, natural ingredients

### **3. Personal Care**
- Score based on: Skin irritants, synthetic fragrances, harsh chemicals
- Display: Safe-for-skin badges, allergen warnings
- Alternatives: Gentle formulas, hypoallergenic, natural

***

## 🔢 SCORING ALGORITHMS

### **Food Products Score (0-100)**

**Real Example Data from Open Food Facts:**
- API: `https://world.openfoodfacts.org/api/v2/product/8901058856507`
- Product: Maggi 2-Minute Noodles
- Data received: `sugars_100g: 5.8`, `saturatedFat_100g: 7.2`, `salt_100g: 2.98`, `proteins_100g: 9.6`, `fiber_100g: 2.0`, `nova_group: 4`

**Scoring Logic:**
- Start at 100
- Subtract: Sugar penalty (×2), Saturated fat (×3), Salt (×7), Excess calories
- Add: Protein bonus (×0.75), Fiber bonus (×1)
- Subtract: NOVA processing penalty (Ultra-processed = -15)
- Result: Maggi scores **34/100** (Poor, Orange)

### **Beauty/Personal Care Score (0-100)**

**Real Example Data from Open Beauty Facts:**
- API: `https://world.openbeautyfacts.org/api/v2/product/{barcode}`
- Product: Typical Shampoo
- Data: `harmful_ingredients: ["Sodium Laureth Sulfate", "Methylparaben", "Fragrance"]`, `vegan: false`, `cruelty_free: true`

**Scoring Logic:**
- Start at 100
- Subtract: Harmful ingredients (×12 each), Parabens (-10), Sulfates (-8), Synthetic fragrance (-5)
- Add: Vegan (+10), Cruelty-free (+10), Paraben-free (+8), Sulfate-free (+8)
- Result: Typical shampoo scores **52/100** (Average, Yellow)

***

## 📊 PRODUCT DETAILS SCREEN LAYOUT

### **Information Hierarchy (Scroll Priority)**

**1. Above Fold (Instantly Visible):**
```
┌─────────────────────────────────┐
│  [Back] SmartLens      [Share]  │ ← Top bar
├─────────────────────────────────┤
│                                 │
│    [Product Image]              │ ← Thumbnail (80×80dp)
│    Product Name                 │ ← Bold, 2 lines max
│    Brand Name                   │ ← Gray, 1 line
│                                 │
│        ┌───────────┐            │
│        │    67     │            │ ← LARGE score circle
│        │   Good    │            │    (200dp diameter)
│        └───────────┘            │
│    Ingredient Safety            │ ← Score type subtitle
│                                 │
│  [🧴 Personal Care]             │ ← Category badge
│                                 │
│  ⚠️ CONTAINS SULFATES           │ ← Critical warnings
│  👃 SYNTHETIC FRAGRANCE          │    (if any, scrollable row)
│                                 │
└─────────────────────────────────┘
```

**2. First Scroll (Key Information):**
```
│  ✓ Cruelty-Free  🐰            │ ← Positive attributes
│  ✓ Dermatologist Tested        │    (horizontal scroll)
│                                 │
│  Ingredient Breakdown           │ ← Section title
│  ├ Harmful: 3 found            │ ← Key metrics
│  ├ Allergens: Fragrance        │    (collapsed by default)
│  └ Skin Irritants: 2           │
│                                 │
│  [Show 5 Better Alternatives]  │ ← Primary CTA (prominent)
│                                 │
```

**3. Second Scroll (Detailed Data):**
```
│  Ingredients (Full List)       │ ← Expandable section
│  > Water, Sodium Laureth...    │    (collapsed, tap to expand)
│                                 │
│  Where to Buy                  │ ← Affiliate section
│  [🛒 Amazon]  [Nykaa]  [Purplle]│
│                                 │
│  Additional Information        │ ← Low-priority details
│  Manufacturer: P&G             │    (collapsed)
│  Country: India                │
│  Size: 180ml                   │
│                                 │
│  Powered by Open Beauty Facts  │ ← Attribution (very small, required)
└─────────────────────────────────┘
```

### **Layout Principles**

**Priority Order:**
1. **Score** → Most important (immediate decision-making)
2. **Warnings** → Safety-critical info (needs attention)
3. **Positive attributes** → Builds trust (quick scan)
4. **Alternatives CTA** → Monetization driver (prominent placement)
5. **Detailed breakdown** → For interested users (expandable)
6. **Full ingredients** → For deep research (collapsible)
7. **Buy links** → Conversion point (above fold on alternatives screen)
8. **Meta info** → Least important (bottom)

**UX Rules:**
- **Don't overwhelm**: Show 3-5 key metrics above fold, hide rest
- **Progressive disclosure**: Use expandable sections for detailed data
- **Visual hierarchy**: Size matters (score = largest, details = smallest)
- **Color coding**: Red warnings grab attention, green reassures
- **Actionable**: Every screen needs clear next step (CTA)

***

## 🗂️ DATA SOURCES

### **Free APIs (No Limits, No Cost)**

**Open Food Facts:**
- Base URL: `https://world.openfoodfacts.org/`
- Get product: `/api/v2/product/{barcode}`
- Get category: `/category/{category-name}.json`
- Coverage: 2.8M+ food products globally

**Open Beauty Facts:**
- Base URL: `https://world.openbeautyfacts.org/`
- Same API structure as food
- Coverage: 1.2M+ beauty/personal care products

**Open Products Facts:**
- Base URL: `https://world.openproductsfacts.org/`
- Same API structure
- Coverage: 400K+ general products

### **Key Data Fields**

**All Products:**
- `product_name`, `brands`, `categories`, `image_url`, `ingredients_text`

**Food-Specific:**
- `nutriments.sugars_100g`, `nutriments.salt_100g`, `nutriments.proteins_100g`
- `nutriscore_grade` (a-e), `nova_group` (1-4)

**Beauty-Specific:**
- `harmful_ingredients[]`, `allergens[]`
- `vegan`, `cruelty_free`, flags for paraben/sulfate-free

***

## 🏗️ TECHNICAL ARCHITECTURE

### **Tech Stack**
- Kotlin, Jetpack Compose, Material 3
- Hilt (DI), Room (cache), DataStore (preferences), Retrofit (API)
- ML Kit Barcode Scanning, CameraX, Coil (images)

### **Project Structure**
```
/data
  /local (Room DB, DataStore)
  /remote (3 API clients: Food, Beauty, Products)
  /repository (Unified product repo, category detector)
/domain
  /models (Unified Product model with nullable fields per category)
  /usecase (Score calculators, alternatives finder, warnings generator)
/ui
  /scanner (Camera + ML Kit barcode detection)
  /product (Adaptive details screen per category)
  /alternatives (Better options list)
  /components (Reusable: ScoreCircle, WarningBadge, CategoryBadge)
```

### **Key Implementation Details**

**Category Detection Flow:**
1. Scan barcode → Try Food API first (largest DB)
2. If not found → Try Beauty API
3. If not found → Try Products API
4. If still not found → Show "Product Not Found"

**Caching Strategy:**
- Store fetched products in Room for 30 days
- Check cache before API call
- Works offline after first scan

**Multi-Category Handling:**
- Unified `Product` data model with nullable fields
- Different scoring algorithms per category (use cases)
- Adaptive UI components (show food charts OR beauty badges)

***

## 🎯 FEATURE BREAKDOWN

### **Core Features (MVP)**

**1. Universal Barcode Scanner**
- ML Kit detects all barcode formats
- Camera preview with scan zone overlay
- Flashlight toggle, manual entry fallback
- Haptic feedback on successful scan

**2. Auto Category Detection**
- Queries all 3 APIs sequentially
- Determines product type automatically
- Shows category badge with color coding

**3. Health/Safety Score Display**
- Large circular progress indicator (0-100)
- Color-coded by score range
- Category-specific subtitle ("Nutritional Health" vs "Ingredient Safety")
- Instant visual understanding

**4. Smart Warnings System**
- Food: High sugar, high salt, ultra-processed
- Beauty: Parabens, sulfates, synthetic fragrance, not cruelty-free
- Displayed as horizontal scrollable badges with emojis
- Only shown if warnings exist (clean UI otherwise)

**5. Category-Specific Details**
- **Food**: Nutri-Score, NOVA, nutritional bar charts (calories, sugar, salt, protein, fiber)
- **Beauty**: Vegan/cruelty-free badges, harmful ingredient count, allergen list
- Show only relevant data per category

**6. Better Alternatives**
- Query same category from appropriate API
- Filter for higher scores only
- Sort by score descending
- Show top 5 with improvement reasons ("50% less sugar", "Paraben-free")
- Each alternative shows: Image, name, score, improvement badge, buy button


**8. Scan History**
- Store all scanned products in Room DB
- List view with product name, category, score
- Sort by recent, filter by category
- Tap to view details again

### **Advanced Features (Phase 2, I will tell when to start implementing these features)** 

**9. Personal Profile**
- Set dietary restrictions (vegan, keto, gluten-free)
- Add allergies (nuts, dairy, fragrance)
- Products automatically checked against profile
- Show personalized warnings

**10. Product Comparison**
- Select 2-4 products from history
- Side-by-side table comparing all metrics
- Highlight best/worst in each category
- Export as image for sharing

**11. Community Insights** (Firebase)
- Show which alternatives other users prefer
- Aggregate user ratings (5-star system)
- Community-submitted better options
- Report incorrect data

**12. AI Meal/Product Scanner** (Google Gemini)
- Scan meal photo, get health analysis
- Scan product without barcode (via image recognition)
- AI-generated recommendations
- 1,500 free requests/day limit

***

## 📱 SCREENS & NAVIGATION

### **Main Screens**

1. **Scanner Screen** → Camera with scan zone, flashlight toggle, manual entry
2. **Product Details** → Score, warnings, breakdown, alternatives CTA, buy links
3. **Alternatives Screen** → List of 5 better options with scores and improvements
4. **Scan History** → List of previously scanned products
5. **Profile Screen** (Phase 2) → Dietary restrictions, allergies, preferences

### **Navigation Flow**
```
Scanner → Product Details → Alternatives → Buy (External Browser)
                ↓
            History ←→ Product Details (revisit)
```

***

## 🎨 UI/UX BEST PRACTICES

### **Do's**
- ✅ Large, bold scores (instant understanding)
- ✅ Progressive disclosure (expand for details)
- ✅ Color-coded everything (visual scanning)
- ✅ Clear CTAs (big buttons, action-oriented)
- ✅ Generous white space (not cluttered)
- ✅ Emoji usage (friendly, approachable)

### **Don'ts**
- ❌ Show all data at once (overwhelming)
- ❌ Long walls of text (TLDR)
- ❌ Small tap targets (<48dp)
- ❌ Hidden CTAs (monetization driver)
- ❌ Generic error messages (be helpful)

***

## 📝 CRITICAL IMPLEMENTATION NOTES

### **API Response Handling**
- All APIs return `status: 1` if product found, `status: 0` if not found
- Check status code before parsing product data
- Handle network errors gracefully (show cached data if available)

### **Null Safety**
- Food products may not have `nutriments` data
- Beauty products may not have `harmful_ingredients` list
- Always check nullable fields before accessing
- Unified model uses nullable fields for category-specific data

### **Performance Optimization**
- Cache API responses for 30 days in Room
- Lazy load images with Coil
- Use LazyColumn for scrolling lists
- Minimize recompositions in Compose

### **Attribution Requirements**
- Display "Powered by Open [Food/Beauty/Products] Facts" in footer
- Link to their website (license requirement)
- No API key needed, but attribution mandatory

***


## ✅ DEVELOPMENT PHASES SUMMARY

**Phase 1:** Project setup, multi-API integration, unified data model  
**Phase 2:** Scanner + category detection working  
**Phase 3:** Multi-category scoring algorithms implemented  
**Phase 4:** Product details screen with adaptive UI  
**Phase 5:** Better alternatives with affiliate links  
**Phase 6:** Warnings, ingredient highlighting, polish  
**Phase 7:** Scan history, navigation complete  
**Phase 8:** Production-ready (error handling, loading states, caching)  
**Phase 9 (Optional):** Advanced features (profiles, comparison, community, AI)

***



**END OF MASTER PROMPT**

***
