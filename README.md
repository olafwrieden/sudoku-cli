# Sudoku CUI Java Game
As part of my Computer Science Degree, one of my assignments was to design and build a software product - a game.

### The Design Brief
This project contains two stages:
1. In Stage 1 **(this repo)**, you will need to develop a Command-line User Interface (CUI) version of the
product. You will need to use text files to store input and output data from the program.
2. In [Stage 2](https://github.com/olafwrieden/sudoku-gui/ "Stage 2 Code"), you will upgrade the product to a Graphical User Interface (GUI) version. Meanwhile, you will include a Database component to the product, implement design patterns and include necessary unit tests.

* Must be developed using Java in the [NetBeans IDE](https://netbeans.org).
* Be bug-free and feature robust error handling.
* Should be easy to build (no complex build configurations).

<details>
  <summary>Expand Criteria & Requirements</summary>
  <p>
    
  Completed | Requirement
:------------ | :-------------|
:heavy_check_mark: | **User Interface (CUI)**<ul><li>Clear and well-designed interface</li><li>The program can handle users’ inputs from the CUI properly</li><li>The interface is easy for users to interact with</li></ul>
:heavy_check_mark: |  **File IO**<ul><li>The program input and output data from/to text files successfully</li></ul>
:heavy_check_mark: |  **Software functionality and usability**<ul><li>The program is easy to compile and run without any manual configurations (e.g. set up input/output files, import .jar files, etc.)</li><li>The program can be easily interacted with without any errors</li><li>The complexity of the functionality</li></ul>
:heavy_check_mark: |  **Software design & implementation**<ul><li>The program can be compiled successfully</li><li>Highly readable code</li><li>Meaningful and appropriate comments</li><li>Executes without runtime errors</li><li>Robust error handling</li><li>Clear class structure</li><li>Complexity and robustness of the functionality</li></ul>
  
  </p>
</details>

### Not using NetBeans IDE?
If you are not using the NetBeans IDE, you may find that the Sudoku grid does not render the red (locked cells) color correctly. I have included a fix in the [Cell.java](https://github.com/olafwrieden/sudoku-cui/blob/master/src/sudoku/Cell.java#L111) class.

<details>
  <summary>Show Fix for Other IDEs</summary>
  <p>
    
```java
@Override
public String toString() {
  if (this.isLocked()) {
    // Uncomment the following line if you don't use NetBeans:
    //return "[" + getUserValue() + "]";

    // NetBeans only! Print locked/generated cells in red:
    return "[" + COLOUR_RED + getUserValue() + COLOUR_RESET + "]";
  }
  return ("[" + (isEmpty() ? "_" : getUserValue()) + "]");
}
```

  </p>
</details>

## Game Overview

#### Main Menu
The *Main Menu* is the very first step view presented to the player. It allows 4 possible options, to start or continue a game, to show the sudoku rules or to exit the game completely.

![Main Menu](/screenshots/main-menu.png)

#### Game Menu
Inside the *Game Menu*, the user is presented with game-related options. To either place or remove a digit, exit the current game with or without saving it's state, or whether the player requires a hint (easy here, they're finite!)

![Game Menu](/screenshots/game-menu.png)

#### Cell Interaction
To edit a cell, it must be specified by the user. This *Cell Interaction* restricts the user to only valid options (aligned with the traditional Sudoku rules).

![Cell Interaction](/screenshots/cell-interaction.png)

#### Saving a Game
Sudoku grids may take a while to complete depending on the player's chosen difficulty level, therefore the current game can be saved, and imported next time. Careful though, only one game can be saved at a time!

![Exporting Sudoku](/screenshots/export-sudoku.png)

#### Completed a Challenge?
After the player fills out the Sudoku grid, the challenge is over and a fun-fact is displayed.

![Sudoku Complete](/screenshots/sudoku-complete.png)

### See something that can be improved?
While this is not an active project of mine, I would love to hear from you. Feel free to submit a Pull Request if you can improve this repository, or open an issue should you encounter a bug. 🐞
