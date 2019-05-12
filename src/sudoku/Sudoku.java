package sudoku;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Sudoku Game Model
 *
 * @author Olaf Wrieden
 * @version 1.0
 */
public class Sudoku {

    // Keyboard Input
    Scanner input = new Scanner(System.in);

    /**
     * Default Sudoku Constructor
     */
    public Sudoku() {
        // Start new Sudoku Game
        welcomeBanner();
        mainMenu();
    }

    /**
     * Application entry point.
     *
     * @param args Optional startup arguments
     */
    public static void main(String[] args) {
        new Sudoku();
    }

    /**
     * Displays the Main Menu in the Console.
     */
    public void mainMenu() {
        int selection = 0;

        do {
            // Display Main Menu Selection
            System.out.println("\n========= Main Menu ==========");
            System.out.println("1: Start a New Game");
            System.out.println("2: Continue your Previous Game");
            System.out.println("3: Show Game Rules");
            System.out.println("4: Exit the Challenge");
            System.out.println("==============================");
            System.out.print("Select: ");

            // Filter user response
            try {
                selection = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException ex) {
                continue;
            }

            // Act on user input        
            switch (selection) {
                case (1): // User wants to Start a New Game
                    Difficulty diff = askDifficulty();
                    if (diff == null) {
                        selection = 0;
                        break;
                    }

                    Generator puzzle = new Generator();
                    puzzle.generateGrid(diff);

                    gameMenu(puzzle.getNewGrid());
                    break;
                case (2): // User wants to Continue a Previous Game
                    Grid saved = importSudoku();
                    if (saved != null) {
                        gameMenu(saved);
                    } else {
                        System.err.println("Sorry, a saved Sudoku could not be retrieved.");
                    }
                    break;
                case (3): // User wants to See Game Rules
                    showRules();
                    break;
                case (4): // Exit Application
                    System.out.println("\nYou have been a good sport!");
                    System.exit(0);
                default: // Default Menu Selection
                    System.out.println("\nInvalid Menu Selection! Try Again.");
            }
        } while (selection < 1 || selection > 4 || selection == 3);
    }

    /**
     * Displays the in-Game Menu in the Console.
     *
     * @param thisChallenge current Sudoku to act upon
     */
    public void gameMenu(Grid thisChallenge) {
        int selection = 0;

        do {
            System.out.println(thisChallenge);
            System.out.println("\n========= Game Menu =========");
            System.out.println("1: Place a Digit");
            System.out.println("2: Remove a Digit");
            System.out.println("3: Exit Without Saving ");
            System.out.println("4: Save & Exit the Challenge");
            System.out.println("5: Give me a hint");
            System.out.println("=============================");
            System.out.print("Select: ");

            // Filter user response
            try {
                String line = input.nextLine().trim();
                selection = Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                continue;
            }

            // Evaluate User Choice
            switch (selection) {
                case (1): // User wants to Place a Digit At

                    // Get Cell
                    Cell cell = specifyCell(thisChallenge);
                    if (cell == null) {
                        selection = 0;
                        break;
                    } else if (cell.isLocked()) {
                        System.out.println(cell.cellDescription());
                        break;
                    }

                    // Get Value
                    int value = specifyValue();
                    if (value == -1) {
                        selection = 0;
                        break;
                    }

                    editCell(cell, thisChallenge, value);
                    break;
                case (2): // User wants to Remove a Digit At

                    // Get Cell
                    Cell emptyCell = specifyCell(thisChallenge);
                    if (emptyCell == null) {
                        selection = 0;
                        break;
                    } else if (emptyCell.isLocked()) {
                        System.out.println(emptyCell.cellDescription());
                        break;
                    }

                    editCell(emptyCell, thisChallenge, 0);
                    break;
                case (3): // User wants to Exit Without Saving
                    mainMenu();
                    break;
                case (4): // Save & Exit Application
                    exportSudoku(thisChallenge);
                    System.out.println("\nYou have been a good sport!");
                    System.exit(0);
                    break;
                case (5): // User wants a hint
                    if (thisChallenge.getHintsUsed() < thisChallenge.getDifficulty().getMaxHints()) {
                        thisChallenge.hint(false);
                        thisChallenge.setHintsUsed();
                        System.out.println("\n--- Summary ---\nUsed Hint: " + thisChallenge.getStringHintsUsed());
                    } else {
                        System.out.println("\n--- Summary ---\nLet's not make it too easy! No more hints.");
                    }
                    break;
                default: // Default Menu Selection
                    System.out.println("\nInvalid Menu Selection! Try Again.");
            }
        } while (selection < 1 || selection > 4 || !thisChallenge.isSolved());

        // Upon solving a puzzle:
        System.out.println(thisChallenge);
        System.out.println(congratulate());
    }

    /**
     * Asks user for their desired Sudoku difficulty.
     *
     * @return the user chosen Sudoku difficulty
     */
    public Difficulty askDifficulty() {
        int selection = -1;

        do {
            System.out.println("\nChoose the difficulty:");
            for (int i = 0; i < Difficulty.values().length; i++) {
                System.out.println((i + 1) + ": " + Difficulty.values()[i]);
            }
            System.out.print("Select: ");

            // Filter user response
            String userInput = input.nextLine().trim();
            if ("x".equals(userInput)) {
                return null;
            } else {
                try {
                    selection = Integer.parseInt(userInput);
                } catch (NumberFormatException ex) {
                    selection = -1;
                }
            }
        } while (selection <= 0 || selection > Difficulty.values().length);
        return Difficulty.values()[selection - 1];
    }

    /**
     * Asks user to specify / choose a cell
     *
     * @param puzzle the Sudoku
     * @return the user-specified cell
     */
    public Cell specifyCell(Grid puzzle) {
        String userInput;

        do {
            System.out.println("\nWhich Cell? (e.g: 1E, x to cancel) ");
            userInput = input.nextLine().trim();

            if ("x".equals(userInput)) {
                return null;
            } else {
                Pattern p = Pattern.compile("^(\\d)([a-zA-Z])$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                Matcher m = p.matcher(userInput);
                if (m.find()) {
                    try {
                        return puzzle.getCell(Integer.valueOf(m.group(1)), m.group(2).toUpperCase().charAt(0));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        userInput = "";
                    }
                } else {
                    userInput = "";
                }
            }
        } while ("".equals(userInput));
        return null;
    }

    /**
     * Asks user to specify / choose a value.
     *
     * @return the user-specified value
     */
    public int specifyValue() {
        String userInput = null;

        while (userInput == null || !userInput.matches("^[1-9]$")) {
            System.out.println("What Value? (e.g: 1 - 9, x to cancel) ");
            userInput = input.nextLine().trim();

            if ("x".equals(userInput)) {
                return -1;
            } else {

                try {
                    if (Integer.valueOf(userInput) > 0 && Integer.valueOf(userInput) < 10) {
                        return Integer.valueOf(userInput);
                    }
                } catch (NumberFormatException ex) {
                    continue;
                }
            }
        }
        return -1;
    }

    /**
     * Performs a value change action on the cell.
     *
     * @param selectedCell the target cell to act on
     * @param puzzle the Sudoku containing the cell
     * @param value the target value to write
     */
    public void editCell(Cell selectedCell, Grid puzzle, int value) {
        if (selectedCell != null) {
            if ((value == 0 || puzzle.meetsConstraints(selectedCell, value) && !selectedCell.isLocked())) {
                selectedCell.setUserValue(value);
                System.out.println("\n--- Summary ---\n" + selectedCell.cellDescription());
            } else if (selectedCell.isLocked()) {
                System.out.println("\n--- Summary ---\nYour chosen cell (" + selectedCell.getPosition() + ") is not editable.");
            } else {
                System.out.println("\n--- Summary ---\nYour digit (" + value + ") does not fit here!");
            }
        } else {
            System.out.println("\n--- Summary ---\nNo changes made.");
        }
    }

    /**
     * Saves the input Sudoku to file.
     *
     * @param puzzle the input Sudoku to storeProvisionalValue
     */
    private void exportSudoku(Grid puzzle) {
        System.out.println("\n--- Saving Sudoku ---");
        System.out.println("PROGRESS: Your Sudoku is being saved...");

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("sudoku.bin", false))) {
            out.writeObject(puzzle);
            System.out.println("EXPORTED: Your Sudoku was saved to disk.");
        } catch (IOException ex) {
            System.err.println("ERROR: Sudoku failed to export to disk.\n\n" + ex);
        } finally {
            System.out.println("---------------------");
        }
    }

    /**
     * Imports the saved Sudoku from file.
     */
    private Grid importSudoku() {
        System.out.println("\n--- Import Sudoku ---");
        System.out.println("PROGRESS: Your Sudoku is being imported...");

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("sudoku.bin"));
            boolean EOF = false;
            while (!EOF) {
                try {
                    Grid f = (Grid) in.readObject();
                    System.out.println("IMPORTED: Your Sudoku was imported.");
                    return f;
                } catch (EOFException eof) {
                    in.close();
                    EOF = true;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        } finally {
            System.out.println("---------------------");
        }
        return null;
    }

    /**
     * Displays the Welcome Banner in the Console.
     */
    public void welcomeBanner() {
        System.out.println("---------------------------------");
        System.out.println(" WELCOME TO THE SUDOKU CHALLENGE ");
        System.out.println("---------------------------------");
    }

    /**
     * Displays Sudoku rules in the Console.
     */
    public void showRules() {
        System.out.println("\n===== Sudoku Rules =====");
        System.out.println("You are presented with a grid of 81 squares, divided into 9 subgrids, each containing 9 squares.");
        System.out.println("\nThe rules are simple:");
        System.out.println("> Each of the 9 blocks has to contain all the numbers 1-9 within its squares.");
        System.out.println("> Each number can only appear once in a row, column or box.");
        System.out.println("> Each vertical nine-square column, or horizontal nine-square line across, within the larger square,\n  must also contain the numbers 1-9, without repetition or omission.");
    }

    /**
     * Congratulate the player upon puzzle completion.
     *
     * @return a completion message and random Sudoku fact
     */
    public String congratulate() {
        String output = "\nCongratulations! You have solved the Puzzle.\n";
        output += "Did you know: " + generateFact() + "?";
        return output;
    }

    /**
     * Generate a random Sudoku fact.
     *
     * @return a random Sudoku fact
     */
    public String generateFact() {
        List<String> facts = new ArrayList<>();
        facts.add("Sudoku puzzles have over 5.47 billion unique solutions");
        facts.add("Contrary to popular belief, Sudoku was invented in America");
        facts.add("Sudokus can likely prevent Alzheimer's disease and Dementia");
        facts.add("In order for a Sudoku to be unique, it must contain at least 17 hints");
        return facts.get(new Random().nextInt(facts.size()));
    }
}
