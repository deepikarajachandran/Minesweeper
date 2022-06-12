import java.util.Scanner;
import java.util.Random;
class MineSweeper {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int row_n = 0, col_n = 0, flagcount = 0, win_count = 0;
        clearScreen();
        System.out.println(">>>>>> Welcome To Minesweeper <<<<<<\n");
        System.out.println("Select Level : ");
        System.out.println("\t 1. Easy");
        System.out.println("\t 2. Medium");
        System.out.println("\t 3. Hard");
        System.out.println("Enter your level : ");
        int level = sc.nextInt();
        switch (level) {
            case 1 :
                row_n = col_n = 9;
                flagcount = 10;
                break;
            case 2 :
                row_n = col_n = 16;
                flagcount = 40;
                break;
            case 3 :
                row_n = 16;
                col_n = 30;
                flagcount = 99;
                break;
            default:
                return;
        }
        int[][] result = new int [row_n][col_n];
        boolean[][] check_matrix = new boolean[row_n][col_n];
        Random random = new Random();
        int count = 0;
        while (count < flagcount) {
            int result_row = random.nextInt(row_n);
            int result_col = random.nextInt(col_n);
            if (result[result_row][result_col] != -2) {
                result[result_row][result_col] = -2;
                if (result_row > 0 && result_col > 0 && result[result_row-1][result_col-1] >= 0) {
                    result[result_row-1][result_col-1] += 1;
                }
                if (result_row > 0 && result[result_row-1][result_col] >= 0) {
                    result[result_row-1][result_col] += 1;
                }
                if (result_row > 0 && result_col < col_n-1 && result[result_row-1][result_col+1] >= 0) {
                    result[result_row-1][result_col+1] += 1;
                }
                if (result_col < col_n-1 && result[result_row][result_col+1] >= 0) {
                    result[result_row][result_col+1] += 1;
                }
                if (result_col > 0 && result[result_row][result_col -1] >= 0) {
                    result[result_row][result_col -1] += 1;
                }
                if (result_row < row_n-1 && result[result_row+1][result_col] >= 0) {
                    result[result_row+1][result_col] += 1;
                }
                if (result_row < row_n-1 && result_col > 0 && result[result_row +1][result_col -1] >= 0) {
                    result[result_row +1][result_col -1] += 1;
                }
                if (result_row < row_n-1 && result_col < col_n-1 && result[result_row +1][result_col +1] >= 0) {
                    result[result_row +1][result_col +1] += 1;
                }
                count++;
            }
        }

        char[][] display = new char[row_n][col_n];
        for (int p_row = 0; p_row < row_n; p_row++) {
            for (int p_col = 0; p_col < col_n; p_col++) {
                display[p_row][p_col] = '-';
            }
        }
        //best Start position
        int[] start_index = best_start(result);
        display[start_index[0]][start_index[1]] = 'X';

        //
        boolean loop = true;
        while (loop) {
            clearScreen();
            System.out.println("------------------- Minesweeper -------------------\n");
            System.out.println("Flag (F) : " + flagcount + "                   " + "Open Count : " + win_count);
            print_matrix(display);
            if (win_count == row_n*col_n - flagcount) {
                print_matrix(display);
                System.out.println("\n-------------- CONGRATULATIONS !! --------------\n");
                System.out.println("               YOU WON THE GAME :)               \n\n");
                System.out.println("1.New Game");
                System.out.println("2. Exit ");
                System.out.println("Enter Choice : ");
                int win_choice = sc.nextInt();
                if (win_choice == 1) {
                    MineSweeper newgame = new MineSweeper();
                    newgame.main(null);
                } else {
                    return;
                }
            }
            System.out.println("Enter row, col :");
            int row = sc.nextInt();
            int col = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Operation : (Open - o / Flag - f)");
            char operation = sc.nextLine().charAt(0);

            switch(operation) {
                case 'o' :
                    if (!check_matrix[row][col]) {
                        if (result[row][col] == -2) {
                            checkBomb(result, display);
                            return;
                        } else if (result[row][col] == 0) {
                            openEmptySpaces(display, check_matrix, result, row, col);
                            win_count = open_mine(display);
                        } else {
                            check_matrix[row][col] = true;
                            display[row][col] = Integer.toString(result[row][col]).charAt(0);
                            win_count++;
                        }
                    } else {
                        System.out.println(">>>>>>>>>>> It's Already Opened / Flagged! <<<<<<<<<<<<<");
                    }
                    break;
                case 'f':
                    flagcount = flag(display, check_matrix, row, col, flagcount);
                    break;
                default:
                    System.out.println(">>>>>>>>>>>>>> Invalid Operation! <<<<<<<<<<<<<<<<<");
            }
        }
    }

    private static void print_matrix(char[][] display) {
        System.out.print("  | ");
        for (int first_row = 0; first_row < display[0].length; first_row++) {
            if (first_row < 10) {
                System.out.print(first_row + " | ");
            } else {
                System.out.print(first_row + "| ");
            }
        }
        System.out.println();
        for (int row = 0; row < display.length; row++) {
            System.out.print(row+" | ");
            for (int col = 0; col < display[0].length; col++) {
                System.out.print(display[row][col] + " | ");
            }
            System.out.println();
        }
    }

    private static int open_mine(char[][] display_matrix) {
        int count = 0;
        for (int row = 0; row < display_matrix.length; row++) {
            for (int col = 0; col < display_matrix.length; col++) {
                if (display_matrix[row][col] != '-' && display_matrix[row][col] != 'F') {
                    count++;
                }
            }
        }
        return count;
    }

    static int[] best_start(int[][] result) {
        int[] index = new int[2];
        int result_row = result.length, result_col = result[0].length;
        int max = 0;
        for (int row = 0; row < result_row; row++) {
            for (int col = 0; col < result_col; col++) {
                int count = 0;
                if (result[row][col] == 0) {
                    if (row > 0 && col > 0 && result[row-1][col-1] == 0) {
                        count += 1;
                    }
                    if (row > 0 && result[row-1][col] == 0) {
                        count += 1;
                    }
                    if (row > 0 && col < result_col-1 && result[row-1][col+1] == 0) {
                        count += 1;
                    }
                    if (col < result_col-1 && result[row][col+1] == 0) {
                        count += 1;
                    }
                    if (col > 0 && result[row][col -1] == 0) {
                        count += 1;
                    }
                    if (row < result_row-1 && result[row+1][col] == 0) {
                        count += 1;
                    }
                    if (row < result_row-1 && col > 0 && result[row +1][col -1] == 0) {
                        count += 1;
                    }
                    if (row < result_row-1 && col < result_col-1 && result[row +1][col +1] == 0) {
                        count += 1;
                    }
                    if (count > max) {
                        max = count;
                        index[0] = row;
                        index[1] = col;
                    }
                }
            }
        }
        return index;
    }

    public static void checkBomb(int[][] result,char[][] playing) {
        clearScreen();
        for (int i = 0; i < playing.length; i++) {
            for (int j = 0; j < playing[0].length; j++) {
                if (result[i][j] == -2) {
                    playing[i][j] = 'B';
                }
            }
        }
        System.out.println("------------------- Minesweeper -------------------\n");
        print_matrix(playing);
        System.out.println("=============== GAME OVER ===============");
        System.out.println("\n         You lost the Game :(        \n");
        System.out.println("         Better luck next time!         \n\n");
        System.out.println("1.New Game");
        System.out.println("2. Exit ");
        System.out.println("Enter Choice : ");
        Scanner sc = new Scanner(System.in);
        int win_choice = sc.nextInt();
        if (win_choice == 1) {
            MineSweeper newgame = new MineSweeper();
            newgame.main(null);
        } else {
            return;
        }
    }

    static int flag(char[][] displayArray, boolean[][] check_matrix, int row, int col, int flagcount) {

        if (displayArray[row][col] == '-') {
            displayArray[row][col] = 'F';
            check_matrix[row][col] = true;
            flagcount--;
            System.out.println("( " + row + "," + col + " ) is flaged.\n Flag Count : " + flagcount);
        } else if (displayArray[row][col] == 'F') {
            displayArray[row][col] = '-';
            check_matrix[row][col] = false;
            flagcount++;
            System.out.println("( " + row + "," + col + " ) is unflaged.\n Flag Count : " + flagcount);

        } else {
            System.out.println(">>>>>>>>>> It's already opened! <<<<<<<<<<<<");
        }
        return flagcount;
    }

    public static void openEmptySpaces(char[][] displaymatrix, boolean[][] matrix, int[][] grid, int i, int j) {
        int grid_row = grid.length, grid_col = grid[0].length;
        if (i == -1 || j == -1 || i >= grid_row || j >= grid_col) {
            return;
        }

        if (grid[i][j] != 0) {
            matrix[i][j] = true;
            displaymatrix[i][j] = Integer.toString(grid[i][j]).charAt(0);
        }

        if (matrix[i][j] == false && grid[i][j] == 0) {
            matrix[i][j] = true;
            displaymatrix[i][j] = ' ';

            openEmptySpaces(displaymatrix, matrix, grid, i, j + 1); // right
            openEmptySpaces(displaymatrix, matrix, grid,i + 1, j); // down
            openEmptySpaces(displaymatrix, matrix, grid, i, j - 1); // left
            openEmptySpaces(displaymatrix, matrix, grid,i - 1, j); // up

            openEmptySpaces(displaymatrix, matrix, grid,i + 1, j + 1); // downright
            openEmptySpaces(displaymatrix, matrix, grid,i - 1, j + 1); // upright
            openEmptySpaces(displaymatrix, matrix, grid,i - 1, j - 1); // upleft
            openEmptySpaces(displaymatrix, matrix, grid,i + 1, j - 1); // downleft
        }
    }

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}