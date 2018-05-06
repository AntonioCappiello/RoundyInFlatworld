package selantoapps.roundyinflatworld.controller;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import selantoapps.roundyinflatworld.R;
import selantoapps.roundyinflatworld.model.Roundy;
import selantoapps.roundyinflatworld.presenter.RoundyViewAnimator;
import selantoapps.roundyinflatworld.presenter.ViewRenderer;
import selantoapps.roundyinflatworld.settings.Direction;
import selantoapps.roundyinflatworld.utils.LogUtility;

/**
 * This class holds the logic of the game. It uses a {@link ViewRenderer} to update the ui on game
 * events and a {@link CollisionDetector} to find possible collision among instances of
 * {@link Roundy}.
 */
public class GameEngine {

    private static final String TAG = GameEngine.class.getSimpleName();

    private RoundyViewAnimator roundyViewAnimator;
    private final int gridSize;
    private ViewRenderer renderer;
    private CollisionDetector collisionDetector;
    private final int cellCount;
    private final int roundyCount;
    private Random random;
    private boolean[] occupiedCells;
    private Roundy[] roundies;

    private static final int DIED = -1;
    private boolean ignoreClicks;
    private int counter; //only used for testing

    public GameEngine(ViewRenderer renderer, CollisionDetector collisionDetector, RoundyViewAnimator roundyViewAnimator, int gridSize,
                      int roundyCount) {
        this.renderer = renderer;
        this.collisionDetector = collisionDetector;
        this.roundyViewAnimator = roundyViewAnimator;
        this.gridSize = gridSize;
        this.cellCount = gridSize * gridSize;
        this.roundyCount = roundyCount;
        random = new Random();
    }

    public void init() {
        counter = -1;
        occupiedCells = new boolean[cellCount];
        roundies = new Roundy[roundyCount];
        drawRoundies();
        renderer.enableInput(true);
    }

    public void restart() {
        if (ignoreClicks) {
            renderer.showToast(R.string.busy);
        } else {
            removeRoundies();
            init();
        }
    }

    private void drawRoundies() {
        int cellIndex, rowIndex, columnIndex;

        for (int i = 0; i < roundyCount - 1; i++) { //-1 because last roundy is drawn only on user action

            cellIndex = pickAFreeCell();
            occupyCell(cellIndex);
            rowIndex = getRowIndexForCellIndex(cellIndex);
            columnIndex = getColumnIndexForCellIndex(cellIndex);
            View roundyView = renderer.createRoundyView(i, rowIndex, columnIndex, R.drawable.circle_solid_green);
            attachClickListener(roundyView, i);

            renderer.addView(roundyView);
            roundies[i] = new Roundy(i, cellIndex, rowIndex, columnIndex, roundyView);
        }
    }

    private void removeRoundies() {
        for (Roundy roundy : roundies) {
            if (roundy != null) {
                renderer.removeView(roundy.getView());
            }
        }
        renderer.refresh();
    }

    private int pickAFreeCell() {

//        Handy cellIndexes when testing specified scenarios in a grid of 8x8
//        counter++;
//        int p[] = {
//                24, 32, //N-S
//                27, 28, //W-E
//                29, 36, // below /
//                20, 27, // above /
//                28, 35, // on /
//                27, 36, // on \
//                20, 29, // above \
//                35, 44, // below \
//                34, 36, 38, 39, // test multiple horizontal crashes
//                36, 20, 12, 52, // test multiple vertical crashes
//                34, 27, 48, 6, // test multiple diagonal / crashes
//                17, 26, 44, 53, // test multiple diagonal \ crashes
//                1};
//        return p[counter];

        int cellIndex;
        do {
            cellIndex = random.nextInt(cellCount);
        } while (occupiedCells[cellIndex]);
        return cellIndex;
    }

    private void occupyCell(int cellIndex) {
        occupiedCells[cellIndex] = true;
    }

    private int getColumnIndexForCellIndex(int cellIndex) {
        return cellIndex % gridSize;
    }

    private int getRowIndexForCellIndex(int cellIndex) {
        if (cellIndex < gridSize) {
            return 0;
        } else {
            return cellIndex / gridSize;
        }
    }

    /**
     * According to the game design, roundies are unhappy if they can collides with other roundies
     * in the possible {@link Direction} of the game.
     * Roundies can move only horizontally, vertically, and diagonally.
     */
    private void findUnhappyRoundies() {
        Log.i(TAG, "findUnhappyRoundies()");

        // reset before calculating new collisions
        for (Roundy roundy : roundies) {
            if (roundy != null) {
                roundy.resetCollisions();
            }
        }

        // find and save potential collisions
        collisionDetector.markCollisions(roundies, new CollisionListener() {
            @Override
            public void onCollisionFound(Roundy roundyA, Roundy roundyB) {
                setUnHappy(roundyA, roundyB);
            }
        });

        LogUtility.logCollisions(TAG, roundies);
    }

    public void addRoundyWithId(int id) {
        int cellIndex = pickAFreeCell();
        occupyCell(cellIndex);
        int rowIndex = getRowIndexForCellIndex(cellIndex);
        int columnIndex = getColumnIndexForCellIndex(cellIndex);

        View roundyView = renderer.createRoundyView(id, rowIndex, columnIndex, R.drawable.circle_solid_yellow);
        attachClickListener(roundyView, id);
        renderer.addView(roundyView);
        roundies[id] = new Roundy(id, cellIndex, rowIndex, columnIndex, roundyView);
        findUnhappyRoundies();
    }

    /**
     * This method attach a click listener to the view associated to the roundy, with the the game
     * logic specified below.
     * <p>
     * When a roundy is clicked then no other action in the game is allowed until all moves are
     * terminated.
     * <p>
     * A roundy can move in one of the {@link Direction} on which he can collides with somebody
     * else. This somebody else is chosen casually.
     * Note: when there are more then one possible collision on the same direction then it is chosen
     * the closest one.
     * We don't want roundies to jump over other roundies :)
     *
     * @param roundyView
     * @param id
     */
    private void attachClickListener(final View roundyView, final int id) {
        roundyView.setTag(id);
        roundyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ignoreClicks) {
                    renderer.showToast(R.string.busy);
                    return;
                }

                int tag = (int) roundyView.getTag();
                Roundy roundyA = roundies[tag];

                if (roundyA.getCollisions().isEmpty()) {
                    renderer.showToast(R.string.no_collisions);
                } else {

                    // Take one at random and make sure it is not covered by others on the same
                    // direction
                    HashMap<Integer, Direction> collisions = roundyA.getCollisions();
                    Integer[] ids = new Integer[collisions.size()];
                    collisions.keySet().toArray(ids);

                    // find random roundy
                    int randomCollisionId = ids[random.nextInt(ids.length)];
                    Log.d(TAG, "random roundy: " + randomCollisionId);

                    Roundy roundyB = roundies[randomCollisionId];
                    Direction roundyBDirection = collisions.get(randomCollisionId);

                    // find other collisions in some direction
                    ArrayList<Integer> possibleClosestIds = new ArrayList<>();
                    for (int otherId : ids) {
                        if (randomCollisionId == otherId) {
                            continue;
                        }
                        if (roundyBDirection == collisions.get(otherId)) { // same direction
                            possibleClosestIds.add(otherId);
                        }
                    }

                    if (!possibleClosestIds.isEmpty()) {
                        // find closed collision to avoid to jump over roundies
                        ArrayList<Roundy> possibleClosestRoundies = new ArrayList<>();
                        possibleClosestRoundies.add(roundyB);
                        for (int i : possibleClosestIds) {
                            possibleClosestRoundies.add(roundies[i]);
                        }
                        roundyB = collisionDetector.findClosest(roundyA, possibleClosestRoundies, roundyBDirection);
                    }

                    Log.d(TAG, "closest roundy: " + roundyB.getId());
                    Log.i(TAG, tag + " is moving towards " + roundyBDirection.name() +
                            " and will collide with " + roundyB.getId());
                    move(roundyA, roundyB, roundyBDirection);
                }
            }
        });
    }

    private void move(Roundy roundyA, Roundy roundyB, Direction direction) {
        ignoreClicks = true;
        roundyViewAnimator.move(roundyA, roundyB, direction);
    }

    public void onMoveAnimationEnd(Roundy roundyA, Roundy roundyB, Direction direction) {
        freeCell(roundyA.getCellIndex());
        roundyA.setCellIndex(roundyB.getCellIndex());
        roundyA.setRowIndex(roundyB.getRowIndex());
        roundyA.setColumnIndex(roundyB.getColumnIndex());
        Log.d(TAG, "transfer movement " + roundyA.getId() + "->" + roundyB.getId());
        transferMovement(roundyB, direction);
    }

    /**
     * When a roundy hits another roundy, then the last one starts move in the same direction and
     * the first one stop in the cell of the collision.
     *
     * @param roundy    the roundy which get the impulse to move
     * @param direction direction of the movement
     */
    private void transferMovement(Roundy roundy, Direction direction) {
        Log.d(TAG, "transferMovement to " + roundy.getId() + " towards " + direction.name());

        // check if roundy can hit anybody in that direction
        if (roundy.getCollisions().values().contains(direction)) {

            // find the closest one in that direction
            StringBuilder logPossibleCollisions = new StringBuilder("Possible collisions");
            Roundy closest = null;
            for (int collidesWithIndex : roundy.getCollisions().keySet()) {
                if (roundy.getCollisions().get(collidesWithIndex) == direction) {
                    Roundy found = roundies[collidesWithIndex];
                    logPossibleCollisions.append("\n").append(found.toString());

                    boolean isCloser = true;

                    if (closest == null) {
                        isCloser = true;
                    } else {
                        switch (direction) {
                            case NORTH:
                            case NORTH_EAST:
                            case NORTH_WEST:
                                isCloser = found.getRowIndex() > closest.getRowIndex();
                                break;
                            case EAST:
                                isCloser = found.getColumnIndex() < closest.getColumnIndex();
                                break;
                            case SOUTH_EAST:
                            case SOUTH_WEST:
                            case SOUTH:
                                isCloser = found.getRowIndex() < closest.getRowIndex();
                                break;
                            case WEST:
                                isCloser = found.getColumnIndex() > closest.getColumnIndex();
                                break;
                        }
                    }

                    if (isCloser) {
                        closest = found;
                    }
                }
            }

            logPossibleCollisions.append("\nclosest: ").append(closest.toString());
            Log.d(TAG, logPossibleCollisions.toString());
            move(roundy, closest, direction);
        } else {
            // nobody to hit, it will fall of the world
            roundyViewAnimator.moveOut(roundy, direction);
        }
    }

    private void freeCell(int cellIndex) {
        occupiedCells[cellIndex] = false;
    }

    public void onMoveOutAnimationEnd(Roundy roundy) {
        Log.i(TAG, "Roundy " + roundy.getId() + " died :(");
        renderer.showToast(R.string.died, String.valueOf(roundy.getId()));

        freeCell(roundy.getCellIndex());
        roundies[roundy.getId()] = null;
        roundy.setCellIndex(DIED);
        renderer.removeView(roundy.getView());

        for (Roundy r : roundies) {
            if (r != null) {
                setHappy(r);
            }
        }
        findUnhappyRoundies();
        ignoreClicks = false;
    }

    private void setUnHappy(Roundy roundy) {
        renderer.showAsHappy(false, roundy.getView());
        roundy.setHappy(false);
        Log.d(TAG, "setUnHappy " + roundy.getId());
    }

    private void setHappy(Roundy roundy) {
        renderer.showAsHappy(true, roundy.getView());
        roundy.setHappy(true);
        Log.d(TAG, "setHappy " + roundy.getId());
    }

    private void setUnHappy(Roundy roundyA, Roundy roundyB) {
        setUnHappy(roundyA);
        setUnHappy(roundyB);
    }
}
