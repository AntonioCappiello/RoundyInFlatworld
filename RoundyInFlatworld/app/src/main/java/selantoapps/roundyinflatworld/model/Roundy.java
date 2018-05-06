package selantoapps.roundyinflatworld.model;

import android.view.View;

import java.util.HashMap;

import selantoapps.roundyinflatworld.settings.Direction;

/**
 * Represent a Roundy in the game with some information associated to it.
 */
public class Roundy {

    private final int id;

    private int cellIndex;

    private int rowIndex;

    private int columnIndex;

    private View view;

    private boolean happy;

    private HashMap<Integer, Direction> collisions;

    /**
     * @param id          roundy identifier
     * @param cellIndex   index of the cell in the grid occupied by this roundy
     * @param rowIndex    row index of the cell occupied
     * @param columnIndex column index of the cell occupied
     * @param view        view which represent the roundy on the grid
     */
    public Roundy(int id, int cellIndex, int rowIndex, int columnIndex, View view) {
        this.id = id;
        this.cellIndex = cellIndex;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.view = view;
        collisions = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public View getView() {
        return view;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    public void addCollision(int index, Direction direction) {
        collisions.put(index, direction);
    }

    public boolean hasCollisionWith(int index) {
        return collisions.get(index) != null;
    }

    public HashMap<Integer, Direction> getCollisions() {
        return collisions;
    }

    public void resetCollisions() {
        collisions.clear();
    }

    @Override
    public String toString() {
        return "Roundy{" +
                "id=" + id +
                ", cellIndex=" + cellIndex +
                ", rowIndex=" + rowIndex +
                ", columnIndex=" + columnIndex +
                ", happy=" + happy +
                '}';
    }
}
