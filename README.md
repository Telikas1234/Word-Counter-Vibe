# 📝 Word Counter (Žodžių skaičiuoklė)

A modern, responsive Android application built with **Kotlin** and **Jetpack Compose** that delivers fast, accurate textual analysis. Word Counter breaks down any input text into four fundamental linguistic metrics: **sentences**, **words**, **writing marks (characters)**, and **numbers**, while maintaining an active calculation history and offering a built-in test-sentence generator.

---

## 📑 Table of Contents

- [Overview & Key Features](#overview--key-features)
- [Tech Stack & Architecture](#tech-stack--architecture)
- [Installation and Running](#installation-and-running)
  - [Prerequisites](#prerequisites)
  - [Method 1: Running with Android Studio (Recommended)](#method-1-running-with-android-studio-recommended)
  - [Method 2: Command Line (Gradle & ADB)](#method-2-command-line-gradle--adb)
  - [Running Unit Tests](#running-unit-tests)
- [How Each Metric is Calculated](#how-each-metric-is-calculated)
  - [1. Sentences](#1-sentences)
  - [2. Words](#2-words)
  - [3. Writing Marks](#3-writing-marks-rašybos-ženklai)
  - [4. Numbers](#4-numbers)
- [Example Inputs and Outputs](#example-inputs-and-outputs)
- [How the Test-Sentence Generator Works](#how-the-test-sentence-generator-works)
- [How the Calculation History Works](#how-the-calculation-history-works)
- [How to Localize the UI Text](#how-to-localize-the-ui-text)
  - [Existing Translations](#existing-translations)
  - [Adding a New Language](#adding-a-new-language)
  - [How String Keys Map to the UI](#how-string-keys-map-to-the-ui)
- [Project Structure](#project-structure)

---

## 🌟 Overview & Key Features

- **⚡ Instant Multi-Metric Analysis**: Calculate Sentences, Words, Writing Marks, and Numbers with a single tap.
- **🛡️ Smart Decimal & Punctuation Protection**: Decimals such as `3.14` or `0,75` are never mistakenly treated as sentence boundaries.
- **🔄 On-Demand Test Sentence Generator**: Quick cycle button to load sample sentences across multiple languages and complex numeric formatting.
- **📜 Live Calculation History**: Visual timeline of all evaluations performed during the session in standard formatted syntax: `"text" => sentences, words, marks, numbers`.
- **⚠️ User Feedback & Validation**: Non-intrusive snackbar alerts notify the user if an evaluation is attempted on empty or blank input.
- **🎨 Material 3 Design**: Clean typography, color-coded metric cards, responsive button flow, and dark/light mode compatibility.
- **🌍 Bilingual Out of the Box**: Fully localized in English (`values`) and Lithuanian (`values-lt`).

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose (BOM `2024.12.01`) with Material 3
- **Architecture**: Clean MVVM (Model-View-ViewModel) with unidirectional data flow
- **State Management**: Kotlin Coroutines & `StateFlow` / `SharedFlow`
- **Testing**: JUnit 4 unit tests covering domain rules, boundary checks, and edge cases
- **Compatibility**: Android 7.0+ (Min SDK 24, Target SDK 35, Compile SDK 37)

```
┌────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI                   │
│   (WordCounterScreen, MetricsDisplayGrid, HistoryCard)  │
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow / Events
┌───────────────────────────┴────────────────────────────┐
│                  WordCounterViewModel                  │
│       (Input State, Metric State, History State)        │
└───────────────▲────────────────────────▲───────────────┘
                │                        │
┌───────────────┴───────────────┐ ┌──────┴───────────────┐
│     TextMetricsCalculator     │ │   SampleSentences    │
│  (Pure Kotlin Domain Engine)  │ │ (Thread-Safe Store)  │
└───────────────────────────────┘ └──────────────────────┘
```

---

## 🚀 Installation and Running

### Prerequisites

1. **Android Studio**: Ladybug (2024.2.1+) or newer.
2. **Java Development Kit (JDK)**: JDK 11 or higher (bundled with Android Studio).
3. **Android Device or Emulator**:
   - Physical device running Android 7.0 (API 24) or higher with **USB Debugging** enabled, OR
   - Android Virtual Device (AVD) configured in Android Studio.

---

### Method 1: Running with Android Studio (Recommended)

1. **Clone or Open the Project**:
   - Open Android Studio.
   - Click **File** > **Open...** and select the project root folder (`VIBECODE`).
2. **Sync Gradle**:
   - Allow Android Studio to automatically download Gradle wrappers and dependencies. If prompted, click **Sync Project with Gradle Files**.
3. **Select a Target Device**:
   - In the top toolbar device selector, pick your running emulator (e.g. `Pixel 6 API 34`) or a connected physical Android device.
4. **Run the App**:
   - Click the green **Run** button (`▶`) or press <kbd>Shift</kbd> + <kbd>F10</kbd>.
   - The app compiles, installs, and launches automatically on your device.

---

### Method 2: Command Line (Gradle & ADB)

Ensure your Android SDK platform-tools are in your system `PATH` (for `adb`).

#### 1. Assemble the Debug APK

- **Windows (PowerShell / Command Prompt)**:
  ```powershell
  .\gradlew.bat assembleDebug
  ```
- **macOS / Linux**:
  ```bash
  chmod +x gradlew
  ./gradlew assembleDebug
  ```

The generated APK will be located at:
```
app/build/intermediates/apk/debug/app-debug.apk
```
*(or under `app/build/outputs/apk/debug/app-debug.apk`)*

#### 2. Install onto Device/Emulator

```bash
adb install -r app/build/intermediates/apk/debug/app-debug.apk
```
*Or use the Gradle task directly:*
```bash
.\gradlew.bat installDebug
```

#### 3. Launch the Application

```bash
adb shell am start -n com.example.wordcounter/.MainActivity
```

---

### Running Unit Tests

To run the local unit tests that verify calculation accuracy across all edge cases:

- **Windows**:
  ```powershell
  .\gradlew.bat test
  ```
- **macOS / Linux**:
  ```bash
  ./gradlew test
  ```

Test reports are generated in HTML format under `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## 📐 How Each Metric is Calculated

All evaluation logic resides in [`TextMetricsCalculator.kt`](app/src/main/java/com/example/wordcounter/domain/TextMetricsCalculator.kt). It is a pure, zero-dependency Kotlin object designed for determinism and testability.

| Metric | What It Measures | Core Calculation Rule |
| :--- | :--- | :--- |
| **Sentences** | Number of grammatical sentences | Delimited by `.` `!` `?` or `\n`; protects decimals; requires at least one alphanumeric character per sentence segment. |
| **Words** | Words containing alphabetic characters | Whitespace-separated tokens stripped of surrounding punctuation; must contain `[a-zA-Z]` (or Unicode letters); excludes standalone numbers. |
| **Writing Marks** | Total character count | `text.length` (includes letters, numbers, spaces, linebreaks, and symbols). |
| **Numbers** | Standalone numeric values | Tokens matching integer/decimal pattern `^[+-]?\d+([.,]\d+)?$` after stripping surrounding punctuation. |

### 1. Sentences

- **Delimiter Matching**: Splits text using the regular expression `[.!?\r\n]+`. This groups consecutive sentence-ending punctuation marks (such as `?!` or `...`) into a single boundary.
- **Decimal Point Protection**: To prevent numbers like `3.14` from splitting a sentence in half, the calculator uses the regex `(?<=\d)\.(?=\d)` to temporarily replace internal decimal dots with a private Unicode placeholder character (`\uFFFF`) before splitting.
- **Valid Segment Check**: Each segment produced by splitting must contain at least one letter or digit (`segment.any { it.isLetterOrDigit() }`). Empty spaces or isolated punctuation between delimiters are discarded.
- **Trailing Sentences**: Text without a trailing period (e.g. `"Labas rytas"`) is legitimately counted as a sentence because it forms a non-empty alphanumeric segment.

### 2. Words

- **Tokenization**: Input is split by whitespace: `Regex("""\s+""")`.
- **Punctuation Trimming**: Each raw token is stripped of surrounding punctuation from both sides:
  - Brackets: `(`, `)`, `[`, `]`, `{`, `}`, `<`, `>`
  - Quotes: `"`, `'`, `„`, `“`, `”`, `«`, `»`
  - Clause marks: `,`, `;`, `:`, `.`, `!`, `?`
  - Currency/Math signs: `%`, `$`, `€`, `£`
- **Exclusion of Pure Numbers**: If the stripped token matches `NUMERIC_REGEX`, it is classified as a number and not a word.
- **Alphabetic Verification**: If not a pure number, the token is verified to contain at least one letter (`token.any { it.isLetter() }`). This ensures symbols or emojis alone are not counted as words.

### 3. Writing Marks (Rašybos ženklai)

- Evaluated directly via `text.length`.
- Accounts for every Unicode code point in the text: alphabetic characters, digits, spaces, tabs, newline escapes (`\r`, `\n`), and symbols.
- *Edge case*: When input consists purely of whitespace (e.g. `"     "`), Writing Marks equals `5`, while Sentences, Words, and Numbers remain `0`.

### 4. Numbers

- Evaluated on trimmed tokens after surrounding punctuation has been removed.
- Validated against:
  ```regex
  ^[+-]?\d+([.,]\d+)?$
  ```
- Supports:
  - Positive and negative integers (`42`, `1361`, `+5`, `-10`).
  - Decimals using dot or comma notation (`3.14`, `0,75`).
  - Numbers enclosed in brackets or followed by punctuation (`(42)`, `15%`, `100€` -> evaluated as `42`, `15`, `100`).

---

## 💡 Example Inputs and Outputs

Here are concrete examples illustrating how the metrics and history entries are formatted:

### Example 1: Basic sentence without terminating punctuation
- **Input Text**:
  ```text
  Labas rytas
  ```
- **Calculated Metrics**:
  - **Sentences**: `1`
  - **Words**: `2` (`"Labas"`, `"rytas"`)
  - **Writing Marks**: `11`
  - **Numbers**: `0`
- **History Output**:
  ```text
  "Labas rytas" => 1, 2, 11, 0
  ```

---

### Example 2: Multiple sentences with digits and period
- **Input Text**:
  ```text
  Rugsėjo 1 prasidėjo karas. Liūdna
  ```
- **Calculated Metrics**:
  - **Sentences**: `2` (`"Rugsėjo 1 prasidėjo karas"`, `"Liūdna"`)
  - **Words**: `4` (`"Rugsėjo"`, `"prasidėjo"`, `"karas"`, `"Liūdna"`)
  - **Writing Marks**: `33`
  - **Numbers**: `1` (`"1"`)
- **History Output**:
  ```text
  "Rugsėjo 1 prasidėjo karas. Liūdna" => 2, 4, 33, 1
  ```

---

### Example 3: Decimals, abbreviations, multiple punctuation, and percentages
- **Input Text**:
  ```text
  Pelnas siekia 3.14 mln. eurų. Tai sudaro 15% prieaugį!
  ```
- **Calculated Metrics**:
  - **Sentences**: `3` (Decimal `3.14` is preserved; segments: `"Pelnas siekia 3.14 mln"`, `" eurų"`, `" Tai sudaro 15% prieaugį"`)
  - **Words**: `7` (`"Pelnas"`, `"siekia"`, `"mln"`, `"eurų"`, `"Tai"`, `"sudaro"`, `"prieaugį"`)
  - **Writing Marks**: `55`
  - **Numbers**: `2` (`"3.14"`, `"15"` after stripping `%`)
- **History Output**:
  ```text
  "Pelnas siekia 3.14 mln. eurų. Tai sudaro 15% prieaugį!" => 3, 7, 55, 2
  ```

---

### Example 4: Clustered punctuation marks
- **Input Text**:
  ```text
  Ar tikrai?!? Taip!
  ```
- **Calculated Metrics**:
  - **Sentences**: `2` (`"Ar tikrai"`, `"Taip"`)
  - **Words**: `3` (`"Ar"`, `"tikrai"`, `"Taip"`)
  - **Writing Marks**: `18`
  - **Numbers**: `0`
- **History Output**:
  ```text
  "Ar tikrai?!? Taip!" => 2, 3, 18, 0
  ```

---

### Example 5: Empty & Whitespace Validation
- **Input Text**: `""` or `"   "`
  - When the user taps **Calculate** (`Skaičiuoti`), calculation is aborted.
  - A snackbar message pops up:
    > *"Please enter text before calculating!"* (or *"Prieš skaičiuodami įveskite tekstą!"*)
  - No empty record is added to the history list.

---

## 🎲 How the Test-Sentence Generator Works

The generator provides instant test data for verification without needing manual typing.

### Implementation Details ([`SampleSentences.kt`](app/src/main/java/com/example/wordcounter/domain/SampleSentences.kt))

1. **Curated Repository**: A built-in list contains sample sentences in both Lithuanian and English, covering different structural challenges (dates, numbers, abbreviations, decimals, exclamations):
   ```kotlin
   val SAMPLES: List<String> = listOf(
       "Labas rytas",
       "Rugsėjo 1 prasidėjo karas. Liūdna",
       "Vilnius yra Lietuvos sostinė jau daugiau nei 700 metų! Ar žinojote tai?",
       "Today is a wonderful day. We counted 42 apples and 3.14 pies!",
       "Kaunas yra antras pagal dydį miestas, įkurtas 1361 metais.",
       "Programavimas su Kotlin ir Android Studio yra labai smagus užsiėmimas!"
   )
   ```
2. **Cyclic Rotation**: A thread-safe method `nextSample()` advances an internal pointer modulo the list size:
   ```kotlin
   @Synchronized
   fun nextSample(): String {
       val sample = SAMPLES[currentIndex]
       currentIndex = (currentIndex + 1) % SAMPLES.size
       return sample
   }
   ```
   This prevents index out of bounds, integer overflow, or race conditions.
3. **UI Integration**:
   - Tapping the **Sample Text** button (`Pavyzdinis tekstas` / `Icons.Default.AutoAwesome`) triggers `WordCounterViewModel.onSampleClicked()`.
   - The sample text is placed directly into the input field.
   - Any stale metric results are cleared until the user clicks **Calculate**.

---

## 📜 How the Calculation History Works

The history records every calculation performed during the current application run.

### Data Model ([`HistoryItem.kt`](app/src/main/java/com/example/wordcounter/model/HistoryItem.kt))

Each history entry stores:
- `id`: A unique UUID (`UUID.randomUUID().toString()`) used as the Compose `LazyColumn` item key for efficient list rendering and animations.
- `text`: The exact raw text submitted by the user.
- `metrics`: The resulting `TextMetrics` object (`sentences`, `words`, `writingMarks`, `numbers`).
- `formattedString`: Formatted output string matching the standard specification:
  ```kotlin
  val formattedString: String
      get() = "\"$text\" => ${metrics.sentences}, ${metrics.words}, ${metrics.writingMarks}, ${metrics.numbers}"
  ```

### Lifecycle & UI Behavior

1. **State Management**: Stored in `WordCounterViewModel` as a `StateFlow<List<HistoryItem>>`.
2. **Order**: New calculations are prepended to the top of the list (`listOf(newItem) + currentHistory`), showing the most recent evaluation first.
3. **Reactive Counter**: The header displays a live badge: `Calculation History (N)`.
4. **Empty State**: If no calculations have been run, a friendly placeholder card is shown: *"No calculations yet. Enter text and click Calculate."*
5. **Session Scope**: History is stored in-memory inside the ViewModel. It survives configuration changes (such as device screen rotations) and resets when the app process is terminated.

---

## 🌐 How to Localize the UI Text

The application is architected around standard Android resource localization principles. Hardcoded strings are strictly avoided; all UI text references `R.string.<name>`.

### Existing Translations

- **Default (English)**: [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml)
- **Lithuanian (`lt`)**: [`app/src/main/res/values-lt/strings.xml`](app/src/main/res/values-lt/strings.xml)

The OS automatically picks the matching file based on the device's system language settings, falling back to English if no match is found.

---

### Adding a New Language

To translate the application into another language (for example, German `de`, Polish `pl`, or Spanish `es`):

1. **Create the New Values Folder**:
   Inside `app/src/main/res/`, create a directory named `values-<language_code>`.
   - German: `app/src/main/res/values-de/`
   - Polish: `app/src/main/res/values-pl/`
   - Spanish: `app/src/main/res/values-es/`
   - French: `app/src/main/res/values-fr/`

2. **Create `strings.xml`**:
   Copy the keys from `app/src/main/res/values/strings.xml` into your new file: `app/src/main/res/values-<language_code>/strings.xml`.

   *Example for German (`values-de/strings.xml`):*
   ```xml
   <resources>
       <string name="app_name">Wortzähler</string>
       <string name="input_label">Zu analysierender Text</string>
       <string name="input_placeholder">Geben Sie hier Text ein...</string>
       <string name="btn_calculate">Berechnen</string>
       <string name="btn_sample">Beispieltext</string>
       <string name="btn_clear">Löschen</string>
       <string name="metric_sentences">Sätze</string>
       <string name="metric_words">Wörter</string>
       <string name="metric_marks">Schriftzeichen</string>
       <string name="metric_numbers">Zahlen</string>
       <string name="empty_input_warning">Bitte Text eingeben vor der Berechnung!</string>
       <string name="history_title">Berechnungsverlauf</string>
       <string name="history_empty">Noch keine Berechnungen vorhanden.</string>
       <string name="results_title">Aktuelle Ergebnisse</string>
   </resources>
   ```

3. **Rebuild the Application**:
   Run `./gradlew assembleDebug` or click **Sync Project with Gradle Files** in Android Studio.

4. **Test the New Locale**:
   - On your device or emulator, open **Settings** > **System** > **Languages & input** > **Languages**.
   - Add your newly translated language and move it to the top of the preference list.
   - Switch back to Word Counter; the UI will immediately render in the selected language.

---

### How String Keys Map to the UI

| String Resource Key | English Default (`values`) | Lithuanian (`values-lt`) | Where It Appears in the UI |
| :--- | :--- | :--- | :--- |
| `app_name` | `Word Counter` | `Žodžių skaičiuoklė` | Top App Bar title & App launcher |
| `input_label` | `Text to analyze` | `Tekstas analizei` | Input field floating label |
| `input_placeholder`| `Enter or paste text here...`| `Įveskite arba įklijuokite tekstą čia...` | Empty input field placeholder |
| `btn_calculate` | `Calculate` | `Skaičiuoti` | Primary action button |
| `btn_sample` | `Sample Text` | `Pavyzdinis tekstas` | Generator action button |
| `btn_clear` | `Clear` | `Išvalyti` | Input clear button & field trailing icon |
| `metric_sentences` | `Sentences` | `Sakiniai` | Sentences metric card label |
| `metric_words` | `Words` | `Žodžiai` | Words metric card label |
| `metric_marks` | `Writing Marks` | `Rašybos ženklai` | Total characters metric card label |
| `metric_numbers` | `Numbers` | `Skaičiai` | Numbers metric card label |
| `empty_input_warning` | `Please enter text before calculating!` | `Prieš skaičiuodami įveskite tekstą!` | Validation popup snackbar |
| `results_title` | `Current Results` | `Dabartiniai rezultatai` | Section heading above metric cards |
| `history_title` | `Calculation History` | `Skaičiavimų istorija` | Section heading above history list |
| `history_empty` | `No calculations yet. Enter text and click Calculate.` | `Istorija tuščia. Įveskite tekstą ir spauskite Skaičiuoti.` | Placeholder card when history is empty |

---

## 📁 Project Structure

```
VIBECODE/
├── app/
│   ├── build.gradle.kts                          # Module-level Gradle build configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml               # App manifest, package & launcher definition
│       │   ├── java/com/example/wordcounter/
│       │   │   ├── MainActivity.kt               # Main activity hosting Compose theme and screen
│       │   │   ├── domain/
│       │   │   │   ├── SampleSentences.kt        # Repository of sample sentences & cycling logic
│       │   │   │   └── TextMetricsCalculator.kt  # Core algorithm calculating the 4 metrics
│       │   │   ├── model/
│       │   │   │   ├── HistoryItem.kt            # History data class with formatted output string
│       │   │   │   └── TextMetrics.kt            # Data model holding the 4 metric integers
│       │   │   └── ui/
│       │   │       ├── WordCounterScreen.kt      # Main Composable screen, cards, and list views
│       │   │       ├── WordCounterViewModel.kt   # UI state holder & user event handler
│       │   │       └── theme/                    # Material 3 colors, typography, and theme
│       │   └── res/
│       │       ├── values/
│       │       │   └── strings.xml               # Default (English) string resources
│       │       └── values-lt/
│       │           └── strings.xml               # Lithuanian string translations
│       └── test/
│           └── java/com/example/wordcounter/domain/
│               └── TextMetricsCalculatorTest.kt  # Comprehensive JUnit 4 test suite
├── gradle/
│   └── libs.versions.toml                        # Version catalog for plugins and dependencies
├── build.gradle.kts                              # Root project Gradle script
├── settings.gradle.kts                           # Gradle project & plugin repository setup
└── README.md                                     # Project documentation
```
