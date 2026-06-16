# Image-to-ASCII Art Processor

An object-oriented Java application that transforms raster images into ASCII art through spatial partitioning, brightness normalization, and pluggable output strategies. The project demonstrates clean separation of concerns across image processing, character matching, algorithm orchestration, and interactive runtime control.

---

## Overview

This repository implements a complete **image-to-ASCII conversion pipeline** built strictly under Object-Oriented Design (OOD) principles. A source image is loaded into an in-memory pixel model, padded to power-of-two dimensions, partitioned into square sub-images, and mapped to characters whose rendered brightness best approximates each region's grayscale intensity.

The application exposes an **interactive shell** that lets users dynamically configure the character set, resolution, rounding strategy, and output format at runtime—without recompiling or restarting the process. The architecture favors single-responsibility classes, strategy-based output, and explicit delegation from the presentation layer (`Shell`) to the algorithm layer (`AsciiArtAlgorithm`).

---

## Repository Structure

```
ImageProccesing_3/
├── README.md
├── .gitignore
└── src/
    ├── image/
    │   ├── Image.java                 # In-memory pixel model and file I/O
    │   └── ImageProcessor.java        # Padding, splitting, brightness calculation
    │
    └── ascii_art/
        ├── AsciiArtSimulator.java     # Application entry point (main)
        ├── Shell.java                 # Interactive CLI controller
        ├── KeyboardInput.java         # Singleton stdin reader
        ├── AsciiArtAlgorithm.java     # Core conversion orchestrator
        │
        ├── img_to_char/
        │   ├── CharConverter.java       # Renders chars to 16×16 boolean grids
        │   └── SubImgCharMatcher.java   # Brightness matching and charset management
        │
        └── output/
            ├── AsciiOutput.java         # Strategy interface
            ├── ConsoleAsciiOutput.java  # Terminal rendering
            └── HtmlAsciiOutput.java     # Browser-viewable HTML export
```

---

## Core Features & Architecture

### Spatial Partitioning

`ImageProcessor.padImage()` expands the source image symmetrically with white pixels until both width and height reach the nearest power of two. `ImageProcessor.splitImage()` then divides the padded image into a grid of **square sub-images**, where the number of columns equals the user-selected **resolution** (characters per row). Each sub-image's average grayscale brightness is computed using the standard luminance formula:

`0.2126·R + 0.7152·G + 0.0722·B`, normalized to `[0.0, 1.0]`.

### HashMap Caching Mechanism

`SubImgCharMatcher` maintains a `HashMap<Character, Double>` cache of raw character brightness values. Once a character's 16×16 raster is analyzed, its brightness is stored and reused on subsequent charset mutations, avoiding redundant font rendering.

`AsciiArtAlgorithm` implements a **single-run brightness cache**: when the same `Image` instance and resolution are reused across consecutive renders, previously computed sub-image brightness matrices are retrieved instead of recalculated—an optimization aligned with incremental interactive sessions.

### Exception Handling

- **Image loading failures** are caught at startup in `AsciiArtSimulator` and surfaced with actionable messages.
- **Invalid shell commands** (malformed `add`/`remove` parameters, out-of-range resolution changes, invalid output or rounding modes) throw `IllegalArgumentException` with specification-compliant error strings.
- **Rendering guardrails** prevent execution when the charset contains fewer than two characters.
- **HTML I/O errors** are logged via `java.util.logging` without crashing the shell loop.

### Strategy Design Pattern for Output

The `ascii_art.output` package defines a polymorphic rendering contract via `AsciiOutput`. Concrete strategies—`ConsoleAsciiOutput` and `HtmlAsciiOutput`—are selected at runtime by `Shell` based on the user's `output` command. This decouples the algorithm from presentation concerns and allows new output formats to be added without modifying core conversion logic.

---

## Interactive Shell Commands

| Command | Description |
|---------|-------------|
| `chars` | Print the active rendering character set |
| `add <param>` | Add characters: single char, `space`, `all`, or range (`a-z`) |
| `remove <param>` | Remove characters symmetrically to `add` |
| `res up` / `res down` | Scale resolution by a factor of 2 (bounded by image dimensions) |
| `res` | Display current resolution |
| `output console` / `output html` | Select output strategy |
| `round abs` / `round up` / `round down` | Select brightness matching strategy |
| `render` | Execute the algorithm and emit output |
| `exit` | Terminate the session |

---

## Technical Skills Demonstrated

- **Object-Oriented Design**: Package-level modularity with clear boundaries between image I/O, algorithm logic, character matching, and I/O strategies.
- **Design Patterns**: Strategy (`AsciiOutput`), Singleton (`KeyboardInput`), and separation of entry point from controller (`AsciiArtSimulator` / `Shell`).
- **Data Structures**: `TreeSet` for ordered charset management, `HashMap` for brightness caching, 2D arrays for spatial partitioning.
- **Image Processing**: Power-of-two padding, sub-image extraction, grayscale normalization.
- **Interactive CLI Design**: Robust command parsing, input validation, and graceful error recovery within a persistent event loop.
- **Java Standard Library**: AWT font rendering, `javax.imageio` for image loading, file I/O for HTML export.

---

## How to Run

### Prerequisites

- **Java JDK 8+** installed and available on your `PATH`
- A sample image file (e.g., `board.jpeg`) accessible from the project directory

### Compile

From the project root, compile all source files:

```bash
javac -d out src/image/*.java src/ascii_art/*.java src/ascii_art/img_to_char/*.java src/ascii_art/output/*.java
```

On Windows (PowerShell), the same command applies:

```powershell
javac -d out src/image/*.java src/ascii_art/*.java src/ascii_art/img_to_char/*.java src/ascii_art/output/*.java
```

### Execute

```bash
java -cp out ascii_art.AsciiArtSimulator path/to/your/image.jpg
```

Example session:

```
>>> chars
0 1 2 3 4 5 6 7 8 9
>>> add a-z
>>> res up
Resolution set to 4.
>>> render
...
>>> exit
```

When `output html` is selected, rendered art is written to `out.html` in the working directory.

---


