/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Class implementing the grid for Killer Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task E and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class KillerSudokuGrid extends SudokuGrid
{
	private int size;
	private int[] values;
	private int min, max;
	public int sum;
	@SuppressWarnings("unused")
	private int cageSize;
	private HashMap<Integer,Cell> board;
	private ArrayList<Cage> cages;
//	private Cage[] boardCages;
	
    public KillerSudokuGrid() {
        super();
        board = new HashMap<>();
    } // end of KillerSudokuGrid()

	public int getSize() {
		return size;
	}

	public int[] getValues() {
		return values;
	}

	public HashMap<Integer,Cell> getBoard() {
		return board;
	}

	public ArrayList<Cage> getCages() {
		return cages;
	}
	
    /* ********************************************************* */


    @Override
    public void initGrid(String filename)
        throws FileNotFoundException, IOException
    {
		Scanner input = new Scanner(new File(filename));
		size = Integer.valueOf(input.nextLine());
		values = new int[size];
		String v = input.nextLine();
		min = Integer.MAX_VALUE;
		max = 0;
		sum = 0;
		for (int i = 0; i < size; i++) {
			values[i] = Integer.valueOf(v.split(" ")[i]);
			sum += values[i];
			if(values[i] < min) {
				min = values[i];
			}
			if(values[i] > max) {
				max = values[i];
			}
		}
		
		cageSize = Integer.valueOf(input.nextLine());
		cages = new ArrayList<>();
		while (input.hasNextLine()) {
			String temp = input.nextLine();
			String[] spl = temp.split(" ");
			ArrayList<Cell> cells = new ArrayList<>();
			int total = Integer.valueOf(spl[0]);
			for(int i=0;i<spl.length-1;i++) {
				int row = Integer.valueOf(spl[i+1].split(",")[0]);
				int column = Integer.valueOf(spl[i+1].split(",")[1]);
				Cell newCell = new Cell(row,column);
				cells.add(newCell);
				board.put(getPosition(row,column), newCell);
			}
			cages.add(new Cage(cells,total));
		}
		input.close();
		
    } // end of initBoard()

    public Integer getPosition(int row, int column) {
		return row * size + column;
	}

	@Override
    public void outputGrid(String filename)
        throws FileNotFoundException, IOException
    {
		FileWriter writer = new FileWriter(filename);
		for (int r = 0;; r++) {
			for (int c = 0;; c++) {
				writer.write(((Integer)board.get(getPosition(r,c)).getValue()).toString());
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
    	
    	// Initial grid does not have any value
    	if(board.get(getPosition(0,0)).getValue()!=0) {
			for (int r = 0;; r++) {
				for (int c = 0;; c++) {
					if(board.get(getPosition(r,c)).getValue()!=0)
						print.append(board.get(getPosition(r,c)).getValue());
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
	    	print.append("\n");
    	}
    	else {
	    	for(Cage cage:cages) {
	    		for(Cell cell:cage.cells) {
	    			print.append("("+cell.row+","+cell.column+")");
	    			print.append(" ");
	    		}
	    		print.append("= ");
	    		print.append(cage.sum);
	    		print.append("\n");
	    	}
	    }

		return print.toString();
    } // end of toString()


    @Override
    public boolean validate() {

		// One value per cell constraint
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				if (board.get(getPosition(r,c)).getValue() == 0)
					return false;

		// Row constraint
		for (int r = 0; r < size; r++) {
			boolean[] check = new boolean[size];
			for (int c = 0; c < size; c++) {
				int value = board.get(getPosition(r,c)).getValue();
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
				int value = board.get(getPosition(r,c)).getValue();
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
						int value = board.get(getPosition(r,c)).getValue();
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
		
		// Cage Constraint
		for(Cage cage:cages) {
			HashSet<Integer> set = new HashSet<>();
			int sum = 0;
			for(int i=0;i<cage.cells.size();i++) {
				int row = cage.cells.get(i).row;
				int column = cage.cells.get(i).column;
				if(set.contains(board.get(getPosition(row,column)).getValue()))
					return false;
				set.add(board.get(getPosition(row,column)).getValue());
				sum += board.get(getPosition(row,column)).getValue();
			}
			if(sum!=cage.sum)
				return false;
		}
		
        return true;
    } // end of validate()
    
    public class Cage implements Comparable<Cage>{
    	private ArrayList<Cell> cells; 
		private HashSet<Integer> list;
		private ArrayList<HashSet<Integer>> valueGuess;
		private int sum;
    	
    	public Cage(ArrayList<Cell> cells, int sum) {
    		this.cells = cells;
    		this.sum = sum;
    		list = new HashSet<>();
    		valueGuess = new ArrayList<>();
    		findElements(0,values.length-cells.size(),cells.size());
			for (Cell cell : cells) {
				cell.setValues(valueGuess);
			}
    	}
    	
    	public ArrayList<Cell> getCells() {
			return cells;
		}

		public int getSum() {
			return sum;
		}
    	
    	public ArrayList<HashSet<Integer>> getValueGuess(){
    		return valueGuess;
    	}

    	// Find all possible values sum to the total
    	private void findElements(int from,int to,int m) {
    		if(m==0) {
    			int sum = 0;
    			for(Integer num:list) {
    				sum += num;
    			}
    			if(sum == this.sum) {
    				valueGuess.add(new HashSet<Integer>(list));
    			}

    		} else if(m>0){
    			for(int j=from;j<=to;j++) {
    				if(list.add((Integer)values[j])) {
	    				findElements(j+1,to+1,m-1);
	    				list.remove((Integer)values[j]);
    				}
    			}
    		}
    	}
    	
    	public int privilege() {
    		// Anm = Cnm * m!;
    		int p = valueGuess.size();
    		for(int i=1;i<=cells.size();i++)
    			p *= i;
    		return p;
    	}
    	
		@Override
		public int compareTo(Cage cage) {
			return cells.size() - cage.cells.size();
		}
    }
    
    public class DancingLinkCell{
    	private int value;
    	private int startPoint;
    	
    	public DancingLinkCell(int value,int startPosition) {
    		this.value = value;
    		this.startPoint = startPosition;
    	}
    	
    	public int getValue() {
    		return value;
    	}
    	
    	public int getStartPoint() {
    		return startPoint;
    	}
    	
    	@Override
    	public boolean equals(Object o) {
    		assert (o instanceof DancingLinkCell);
    		DancingLinkCell temp = (DancingLinkCell)o;
    		if(temp.value == value && temp.startPoint == startPoint)
    			return true;
    		return false;
    	}
    	
    	@Override
    	public String toString() {
    		return String.format("[Value: %d, StartPoint: %d]",value,startPoint );
    	}
    }
    
    public class Cell implements Comparable<Cell>{
    	private int row;
    	private int column;
    	private int value;
    	private ArrayList<Integer> values;
    	private ArrayList<DancingLinkCell> dancingLinkCells;
    	
      	public Cell(int row,int column) {
    		this.row = row;
    		this.column = column;
    		this.values = new ArrayList<>();
    		this.dancingLinkCells = new ArrayList<>();
    		this.value = 0;
    	}
      	
      	public void setValues(ArrayList<HashSet<Integer>> valueGuess) {
    		for(HashSet<Integer> set:valueGuess) {
    			Iterator<Integer> iterator = set.iterator();
    			int position = 0;
    			while(iterator.hasNext()) {
    				int v = iterator.next();
        			DancingLinkCell dlc = new DancingLinkCell(v,position);
        			if(!dancingLinkCells.contains(dlc))
        				dancingLinkCells.add(dlc);
    				if(!values.contains((Integer)v))
    					values.add((Integer)v);
    				position += v;
    			}
    		}
      	}
      	
      	public ArrayList<DancingLinkCell> getDancingLinkCells(){
      		return dancingLinkCells;
      	}
      	
      	public ArrayList<Integer> getValues(){
      		return values;
      	}
      	
		public void setValue(int value) {
			this.value = value;
		}
    	
    	public int getRow() {
    		return row;
    	}
    	
    	public int getColumn() {
    		return column;
    	}
    	
    	public int getValue() {
    		return value;
    	}

    	@Override
    	public boolean equals(Object o) {
    		assert (o instanceof Cell);
    		Cell cell = (Cell)o;
    		if(cell.compareTo(this)==0)
    			return true;
    		return false;
    	}
    	
		@Override
		public int compareTo(Cell cell) {
			return (row-cell.row)*size + column - cell.column;
		}
    	
    }

} // end of class KillerSudokuGrid
