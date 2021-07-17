/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 * @author Benhao Qu
 */
package grid;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class implementing the grid for standard Sudoku. Extends SudokuGrid (hence
 * implements all abstract methods in that abstract class). You will need to
 * complete the implementation for this for task A and subsequently use it to
 * complete the other classes. See the comments in SudokuGrid to understand what
 * each overriden method is aiming to do (and hence what you should aim for in
 * your implementation).
 */
public class StdSudokuGrid extends SudokuGrid {
	private int size;
	private int[] values;
	private HashMap<Integer, Integer> board;

	public StdSudokuGrid() {
		super();
		board = new HashMap<>();
	} // end of StdSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {
		Scanner input = new Scanner(new File(filename));
		size = Integer.valueOf(input.nextLine());
		values = new int[size];
		String v = input.nextLine();
		for (int i = 0; i < size; i++)
			values[i] = Integer.valueOf(v.split(" ")[i]);
		while (input.hasNextLine()) {
			String temp = input.nextLine();
			int row = Integer.valueOf(temp.split(",")[0]);
			int column = Integer.valueOf(temp.split(",")[1].split(" ")[0]);
			int value = Integer.valueOf(temp.split(",")[1].split(" ")[1]);
			board.put(getPosition(row, column), value);
		}
		input.close();
	} // end of initBoard()

	@Override
	public void outputGrid(String filename) throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(filename);
		for (int r = 0;; r++) {
			for (int c = 0;; c++) {
				writer.write(board.get(getPosition(r, c)).toString());
				if (c == size - 1)
					break;
				writer.write(",");
			}
			if (r == size - 1)
				break;
			writer.write("\n");
		}
		writer.close();
	} // end of outputBoard()

	@Override
	public String toString() {
		StringBuilder print = new StringBuilder("");
		for (int r = 0;; r++) {
			for (int c = 0;; c++) {
				if(board.containsKey(getPosition(r, c)))
					print.append(board.get(getPosition(r, c)));
				else
					print.append(" ");
				if (c == size - 1)
					break;
				print.append("\t");
			}
			if (r == size - 1)
				break;
			print.append("\n\n");
		}

		// placeholder
		return print.toString();
	} // end of toString()

	@Override
	public boolean validate() {

		// One value per cell constraint
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				if (!board.containsKey(getPosition(r, c)))
					return false;

		// Row constraint
		for (int r = 0; r < size; r++) {
			boolean[] check = new boolean[size];
			for (int c = 0; c < size; c++) {
				int value = board.get(getPosition(r, c));
				for (int i = 0; i < size; i++)
					if (value == values[i]) {
						check[i] = true;
						break;
					}
			}
			for (int i = 0; i < size; i++)
				if (check[i] == false)
					return false;
		}

		// Column constraint
		for (int c = 0; c < size; c++) {
			boolean[] check = new boolean[size];
			for (int r = 0; r < size; r++) {
				int value = board.get(getPosition(r, c));
				for (int i = 0; i < size; i++)
					if (value == values[i]) {
						check[i] = true;
						break;
					}
			}
			for (int i = 0; i < size; i++)
				if (check[i] == false)
					return false;
		}

		// Box Constraint
		int box = (int) Math.pow(size, 0.5);
		for (int i = 0; i < box; i++) {
			for (int j = 0; j < box; j++) {
				boolean[] check = new boolean[size];
				for (int r = i * box; r < (i + 1) * box; r++) {
					for (int c = j * box; c < (j + 1) * box; c++) {
						int value = board.get(getPosition(r, c));
						for (int k = 0; k < size; k++)
							if (value == values[k]) {
								check[k] = true;
								break;
							}
					}
				}
				for (int k = 0; k < size; k++)
					if (check[k] == false)
						return false;
			}
		}

		// placeholder
		return true;
	} // end of validate()

	public int getPosition(int row, int column) {
		return row * size + column;
	}
	
	public int getSize() {
		return size;
	}

	public int[] getValues() {
		return values;
	}

	public HashMap<Integer, Integer> getBoard() {
		return board;
	}
	
} // end of class StdSudokuGrid
