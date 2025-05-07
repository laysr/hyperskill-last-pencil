package lastpencil;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        final String player = "John";
        final String bot = "Jack";
        final char pencilSign = '|';
        final int maxPencilsToRemove = 3;

        Game game = new Game(player, bot, pencilSign, maxPencilsToRemove);

        game.start();
    }
}

class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private final char pencilSign;
    private final int maxPencilsToRemove;
    private final String player;
    private final String bot;
    private String playerOnMove;
    private int pencilsCount;

    Game(String player, String bot, char pencilSign, int maxPencilsToRemove) {
        this.player = player;
        this.bot = bot;
        this.pencilSign = pencilSign;
        this.maxPencilsToRemove = maxPencilsToRemove;
    }

    void start() {
        init();
        run();
    }

    private void init() {
        setPencilsCount(inputInitialPencilsCount());
        setPlayerOnMove(inputPlayerOnMove());
    }

    private void run() {
        while (! isZeroPencilsCount()) {
            printPencils();
            printPlayerOnMove();
            if (isBotLeftWithOnePencil()) {
                System.out.println("1");
                setPencilsCount(0);
                moveTurnToNextPlayer();
                break;
            }
            makeNextMove();
        }
        printWinner();
    }

    private void setPencilsCount(int count) {
        pencilsCount = count;
    }

    private void setPlayerOnMove(String player) {
        playerOnMove = player;
    }

    private int inputInitialPencilsCount() {
        int initialPencilsCount = 0;
        boolean isValidInput = false;

        System.out.println("How many pencils would you like to use:");

        while (! isValidInput) {
            try {
                String input = scanner.nextLine();
                initialPencilsCount  = Integer.parseInt(input);

                if (initialPencilsCount == 0) {
                    throw new PencilCountNotPositiveException();
                } else if (initialPencilsCount < 0) {
                    throw new NumberFormatException();
                }

                isValidInput = true;
            } catch (NumberFormatException e) {
                System.out.println("The number of pencils should be numeric");
            } catch (PencilCountNotPositiveException e) {
                System.out.println(e.getMessage());
            }
        }

        return initialPencilsCount;
    }

    private String inputPlayerOnMove() {
        String playerOnMove = "";
        boolean isValidInput = false;

        System.out.printf("Who will be the first (%s, %s):\n", player, bot);

        while (! isValidInput) {
            try {
                playerOnMove = scanner.nextLine();

                if (! playerOnMove.equals(player) && ! playerOnMove.equals(bot)) {
                    throw new WrongNameException(player, bot);
                }

                isValidInput = true;
            } catch (WrongNameException e) {
                System.out.println(e.getMessage());
            }
        }

        return player.equals(playerOnMove) ? player : bot;
    }

    private void printPencils() {
        final String pens = Character.toString(pencilSign).repeat(pencilsCount);
        System.out.println(pens);
    }
    private void printPlayerOnMove() {
        System.out.printf("%s's turn!\n", playerOnMove);
    }

    private boolean isZeroPencilsCount() {
        return pencilsCount <= 0;
    }

    private boolean isBotsTurn() {
        return playerOnMove.equals(bot);
    }
    
    private boolean isBotLeftWithOnePencil() {
        return pencilsCount == 1 && isBotsTurn();
    }

    private void makeNextMove() {
        int pencilsCountToRemove = inputPencilsCountToRemove();
        decreasePencilsCount(pencilsCountToRemove);
        moveTurnToNextPlayer();
    }

    private int winningStrategyPencils() {
        if (pencilsCount % 4 == 0) {
            return 3;
        } else if (pencilsCount % 4 == 3) {
            return 2;
        } else if (pencilsCount % 4 == 2) {
            return 1;
        }
        int randomLowerBound = 1;
        int randomUpperBound = Math.min(pencilsCount, 4);
        return random.nextInt(randomLowerBound, randomUpperBound);
    }

    private int inputPencilsCountToRemove() {
        int pencilsCountToRemove = 0;
        boolean isValidInput = false;

        while (! isValidInput) {
            try {
                if (isBotsTurn()) {
                    pencilsCountToRemove = winningStrategyPencils();
                } else {
                    pencilsCountToRemove = scanner.nextInt();
                }

                if (pencilsCountToRemove < 1 || pencilsCountToRemove > maxPencilsToRemove) {
                    throw new NotAllowedPencilsCountToRemoveException();
                } else if (pencilsCountToRemove > pencilsCount) {
                    throw new TooManyPencilsToRemoveException();
                }

                isValidInput = true;
            } catch(InputMismatchException e) {
                System.out.println("Possible values: '1', '2' or '3");
            } catch(NotAllowedPencilsCountToRemoveException | TooManyPencilsToRemoveException e) {
                System.out.println(e.getMessage());
            } finally {
                if (isBotsTurn()) {
                    System.out.println(pencilsCountToRemove);
                } else {
                    scanner.nextLine();
                }
            }
        }

        return pencilsCountToRemove;
    }

    private void decreasePencilsCount(int pencilsCountToRemove) {
        pencilsCount -= pencilsCountToRemove;
    }

    private void moveTurnToNextPlayer() {
        if (playerOnMove.equals(player)) {
            playerOnMove = bot;
        } else {
            playerOnMove = player;
        }
    }

    private void printWinner() {
        System.out.printf("%s won!\n", playerOnMove);
    }
}

class PencilCountNotPositiveException extends Exception {
    PencilCountNotPositiveException() {
        super("The number of pencils should be positive");
    }
}

class WrongNameException extends Exception {
    WrongNameException(String nameOne, String nameTwo) {
        super(String.format("Choose between %s and %s", nameOne, nameTwo));
    }
}

class NotAllowedPencilsCountToRemoveException extends Exception {
    NotAllowedPencilsCountToRemoveException() {
        super("Possible values: '1', '2' or '3'");
    }
}

class TooManyPencilsToRemoveException extends Exception {
    TooManyPencilsToRemoveException() {
        super("Too many pencils were taken");
    }
}