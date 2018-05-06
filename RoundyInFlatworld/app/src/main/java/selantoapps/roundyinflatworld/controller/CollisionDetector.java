package selantoapps.roundyinflatworld.controller;

import android.util.Log;

import java.util.ArrayList;

import selantoapps.roundyinflatworld.model.Roundy;
import selantoapps.roundyinflatworld.settings.Direction;

public class CollisionDetector {

    private final String TAG = CollisionDetector.class.getSimpleName();
    private int gridSize;

    public CollisionDetector(int gridSize) {
        this.gridSize = gridSize;
    }

    /**
     * Determine if roundyA will collide with roundyB if roundyA will start moving along the
     * direction specified
     *
     * @param roundyA
     * @param roundyB
     * @return true in case of collision, false otherwise
     */
    public boolean collides(Roundy roundyA, Roundy roundyB, Direction direction) {
        switch (direction) {
            case NORTH:
                return collidesN(roundyA, roundyB);
            case NORTH_EAST:
                return collidesNE(roundyA, roundyB);
            case EAST:
                return collidesE(roundyA, roundyB);
            case SOUTH_EAST:
                return collidesSE(roundyA, roundyB);
            case SOUTH:
                return collidesS(roundyA, roundyB);
            case SOUTH_WEST:
                return collidesSW(roundyA, roundyB);
            case WEST:
                return collidesW(roundyA, roundyB);
            case NORTH_WEST:
                return collidesNW(roundyA, roundyB);
        }
        return false;
    }

    private boolean collidesNW(Roundy roundyA, Roundy roundyB) {
        int rowIndex = roundyA.getRowIndex();
        int columnIndex = roundyA.getColumnIndex();

        // search N-W
        while (rowIndex > 0 && columnIndex > 0) {
            rowIndex--;
            columnIndex--;
            if (rowIndex == roundyB.getRowIndex() && columnIndex == roundyB.getColumnIndex()) {
                Log.w(TAG, roundyA.getId() + " collidesNW with " + roundyB.getId());
                return true;
            }
        }
        return false;
    }

    private boolean collidesSE(Roundy roundyA, Roundy roundyB) {
        int rowIndex = roundyA.getRowIndex();
        int columnIndex = roundyA.getColumnIndex();

        // search S-E
        while (rowIndex < gridSize && columnIndex < gridSize) {
            rowIndex++;
            columnIndex++;
            if (rowIndex == roundyB.getRowIndex() && columnIndex == roundyB.getColumnIndex()) {
                Log.w(TAG, roundyA.getId() + " collidesSE with " + roundyB.getId());
                return true;
            }
        }

        return false;
    }

    private boolean collidesNE(Roundy roundyA, Roundy roundyB) {
        int rowIndex = roundyA.getRowIndex();
        int columnIndex = roundyA.getColumnIndex();

        // search N-E
        while (rowIndex > 0 && columnIndex < gridSize) {
            rowIndex--;
            columnIndex++;
            if (rowIndex == roundyB.getRowIndex() && columnIndex == roundyB.getColumnIndex()) {
                Log.w(TAG, roundyA.getId() + " collidesNE with " + roundyB.getId());
                return true;
            }
        }

        return false;
    }

    private boolean collidesSW(Roundy roundyA, Roundy roundyB) {
        int rowIndex = roundyA.getRowIndex();
        int columnIndex = roundyA.getColumnIndex();

        // search S-W
        while (rowIndex < gridSize && columnIndex > 0) {
            rowIndex++;
            columnIndex--;
            if (rowIndex == roundyB.getRowIndex() && columnIndex == roundyB.getColumnIndex()) {
                Log.w(TAG, roundyA.getId() + " collidesSW with " + roundyB.getId());
                return true;
            }
        }

        return false;
    }

    private boolean collidesN(Roundy roundyA, Roundy roundyB) {
        boolean collide = roundyA.getColumnIndex() == roundyB.getColumnIndex() &&
                roundyA.getRowIndex() > roundyB.getRowIndex();
        if (collide) {
            Log.w(TAG, roundyA.getId() + " collidesN with " + roundyB.getId());
        }
        return collide;
    }

    private boolean collidesS(Roundy roundyA, Roundy roundyB) {
        boolean collide = roundyA.getColumnIndex() == roundyB.getColumnIndex() &&
                roundyA.getRowIndex() < roundyB.getRowIndex();
        if (collide) {
            Log.w(TAG, roundyA.getId() + " collides with " + roundyB.getId());
        }
        return collide;
    }

    private boolean collidesW(Roundy roundyA, Roundy roundyB) {
        boolean collide = roundyA.getRowIndex() == roundyB.getRowIndex() &&
                roundyA.getColumnIndex() > roundyB.getColumnIndex();
        if (collide) {
            Log.w(TAG, roundyA.getId() + " collides with " + roundyB.getId());
        }
        return collide;
    }

    private boolean collidesE(Roundy roundyA, Roundy roundyB) {
        boolean collide = roundyA.getRowIndex() == roundyB.getRowIndex() &&
                roundyA.getColumnIndex() < roundyB.getColumnIndex();
        if (collide) {
            Log.w(TAG, roundyA.getId() + " collides with " + roundyB.getId());
        }
        return collide;
    }

    /**
     * Find the roundy in roundies which is the closest to roundyA along the collision specified
     * collision direction
     *
     * @param roundyA   reference roundy
     * @param roundies  roundies to compare with
     * @param direction of the collision from roundyA towards roundies
     * @return the closest roundy to roundyA
     */
    public Roundy findClosest(Roundy roundyA, ArrayList<Roundy> roundies, Direction direction) {
        Roundy closest = null;
        for (Roundy found : roundies) {
            if (closest == null) {
                closest = found;
            } else {
                // at each iteration compare the current closest with the new found
                closest = findClosest(roundyA, closest, found, direction);
            }
        }
        return closest;
    }

    /**
     * Like {@link #findClosest(Roundy, ArrayList, Direction)} but instead of an array list of
     * roundies, there are only two to compare with.
     * <p>
     * In case of tie, the roundy with the smallest id is returned.
     *
     * @param roundyA
     * @param roundyB
     * @param roundyC
     * @param direction
     * @return
     */
    private static Roundy findClosest(Roundy roundyA, Roundy roundyB, Roundy roundyC, Direction direction) {
        Roundy closest;

        // calculate deltas with roundyA based on row and column grid indexes
        // which is faster than calculating the pixel distance among the roundy's view
        int deltaB = 0, deltaC = 0;
        switch (direction) {
            case NORTH:
            case SOUTH:
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_EAST:
            case SOUTH_WEST:
                deltaB = Math.abs(roundyA.getRowIndex() - roundyB.getRowIndex());
                deltaC = Math.abs(roundyA.getRowIndex() - roundyC.getRowIndex());
                break;
            case EAST:
            case WEST:
                deltaB = Math.abs(roundyA.getColumnIndex() - roundyB.getColumnIndex());
                deltaC = Math.abs(roundyA.getColumnIndex() - roundyC.getColumnIndex());
                break;
        }

        // decide closest roundy based on deltas from roundyA
        if (deltaB == deltaC) {
            // break tie with roundy'id
            if (roundyB.getId() < roundyC.getId()) {
                closest = roundyB;
            } else {
                closest = roundyC;
            }
        } else if (deltaB < deltaC) {
            closest = roundyB;
        } else {
            closest = roundyC;
        }

        return closest;
    }

    public void markCollisions(Roundy[] roundies, CollisionListener listener) {
        for (Roundy roundyA : roundies) {

            if (roundyA == null) {
                continue;
            }

            for (Roundy roundyB : roundies) {

                if (roundyB == null || roundyA == roundyB || roundyA.hasCollisionWith(roundyB.getId())) {
                    continue;
                }

                if (collides(roundyA, roundyB, Direction.NORTH)) {

                    listener.onCollisionFound(roundyA, roundyB);
                    roundyA.addCollision(roundyB.getId(), Direction.NORTH);
                    roundyB.addCollision(roundyA.getId(), Direction.SOUTH);  //save extra check

                } else if (collides(roundyA, roundyB, Direction.WEST)) {

                    listener.onCollisionFound(roundyA, roundyB);
                    roundyA.addCollision(roundyB.getId(), Direction.WEST);
                    roundyB.addCollision(roundyA.getId(), Direction.EAST); //save extra check

                } else if (collides(roundyA, roundyB, Direction.NORTH_WEST)) {

                    listener.onCollisionFound(roundyA, roundyB);
                    roundyA.addCollision(roundyB.getId(), Direction.NORTH_WEST);
                    roundyB.addCollision(roundyA.getId(), Direction.SOUTH_EAST);  //save extra check

                } else if (collides(roundyA, roundyB, Direction.NORTH_EAST)) {

                    listener.onCollisionFound(roundyA, roundyB);
                    roundyA.addCollision(roundyB.getId(), Direction.NORTH_EAST);
                    roundyB.addCollision(roundyA.getId(), Direction.SOUTH_WEST);  //save extra check
                }
            }
        }
    }
}
