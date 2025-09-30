# InkBall

## Project Overview
This project is a prototype implementation of the classic **Inkball** game, developed in **Java** using the **Processing library** for graphics and **Gradle** as the dependency manager.  

The game objective is to guide coloured balls into matching holes by drawing lines on the screen. Balls bounce off walls, drawn lines, and edges. Scoring depends on successful or failed captures, and the game ends when all balls are captured.


---

## Features
- Load level layouts from JSON config and text-based map files.  
- Spawning and movement of coloured balls with random velocity.  
- Player interactions:  
  - Draw lines (left mouse button).  
  - Remove lines (right mouse button).  
  - Pause (spacebar).  
  - Restart level/game (`r`).  
- Ball collision with walls, drawn lines, and edges.  
- Holes with attraction effect, shrinking animation, and colour-based scoring.  
- Configurable scoring, timers, and spawn intervals.  
- Support for multiple levels with persistent score across levels.  

---

## File Structure
```
├── build.gradle               # Gradle build file
├── config.json                # Game configuration file
├── layout/                    # Text files for level layouts
│   └── level1.txt
│   └── level2.txt
├── src/
│   ├── main/java/             # Java source files
│   │   └── inkball/           # Core game classes
│   └── test/java/             # JUnit test cases
├── resources/
│   └── inkball/               # Sprites (balls, walls, holes, etc.)
└── README.md
```

---

## Setup and Running
### Requirements
- **Java 8** (must compile and run on this version)  
- **Gradle** (project uses Gradle wrapper if provided)

### Running the Game
```bash
# Compile and run
gradle run
```

### Running Tests
```bash
# Execute unit tests with coverage
gradle test jacocoTestReport
```

Coverage reports will be generated in:
```
build/reports/jacoco/test/html/index.html
```

---

## Controls
- **Left Mouse Button** → Draw line  
- **Right Mouse Button** → Remove line  
- **Spacebar** → Pause/Resume  
- **R** → Restart current level / Restart game  

---

## Design Notes
- **OOP principles** applied: separation of concerns across entities (Ball, Wall, Hole, Level, etc.), interfaces for extensibility, and class hierarchy for game objects.  
- **Collision handling** follows vector reflection physics.  
- **Test-driven development** used to achieve >90% coverage (common and edge cases).  
- **Extension implemented**: [Describe your chosen extension here, e.g., one-way walls, key activators, or timed tiles].

---

## Testing
- Implemented **JUnit tests** in `src/test/java`.  
- Covered ball movement, collision detection, scoring, timers, and extension functionality.  
- Achieved >90% branch and instruction coverage using **JaCoCo**.  

---

## Submission
- **Code & config** submitted via Ed (`src` + `build.gradle` + sample configs/layouts).  
- **Report & UML** submitted separately via Canvas.  
- **Demo** in Week 12 tutorial.

---

## References
- [Processing Javadocs](https://processing.github.io/processing-javadocs/core/)  
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)  
- [Oracle JavaDoc Guide](https://www.oracle.com/technetwork/java/javase/documentation/index-137868.html)  
