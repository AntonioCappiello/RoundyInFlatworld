package selantoapps.roundyinflatworld.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import selantoapps.roundyinflatworld.model.Roundy;
import selantoapps.roundyinflatworld.settings.Constants;
import selantoapps.roundyinflatworld.settings.Direction;
import selantoapps.roundyinflatworld.utils.LogUtility;

public class RoundyViewAnimator {

    private static final String TAG = RoundyViewAnimator.class.getSimpleName();

    private Drawable onTheMoveBg;

    private float roundySize;

    private float gridViewWidth;

    private float gridViewHeight;

    private float cellSize;

    private RoundyAnimationListener listener;

    public RoundyViewAnimator(Drawable onTheMoveBg, RoundyAnimationListener listener) {
        this.onTheMoveBg = onTheMoveBg;
        this.listener = listener;
    }

    /**
     * When grid view is drawn then it is possible to initialize this animator with the right
     * parameters
     *
     * @param roundySize
     * @param gridViewWidth
     * @param gridViewHeight
     * @param cellSize
     */
    public void init(int roundySize, int gridViewWidth, int gridViewHeight, int cellSize) {
        this.roundySize = roundySize;
        this.gridViewWidth = gridViewWidth;
        this.gridViewHeight = gridViewHeight;
        this.cellSize = cellSize;
    }

    /**
     * Move roundyA to roundyB
     *
     * @param roundyA
     * @param roundyB
     * @param direction
     */
    public void move(final Roundy roundyA, final Roundy roundyB, final Direction direction) {
        Log.d(TAG, "move()");
        View viewA = roundyA.getView();
        View viewB = roundyB.getView();

        LogUtility.logMove(viewA, viewB);

        viewA.setBackground(onTheMoveBg);
        viewA.animate().x(viewB.getX()).y(viewB.getY()).setDuration(Constants.MOVE_SPEED).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onMoveAnimationEnd(roundyA, roundyB, direction);
            }
        });
    }

    /**
     * Move roundy in the specified direction until it falls off the grid.
     *
     * @param roundy
     * @param direction
     */
    public void moveOut(final Roundy roundy, Direction direction) {
        Log.d(TAG, "moveOut() " + roundy.getId() + " towards " + direction.name());
        final View view = roundy.getView();
        float startX = view.getX();
        float startY = view.getY();
        float endX = 0, endY = 0;

        switch (direction) {
            case NORTH:
                endX = startX;
                endY = -roundySize;
                break;
            case EAST:
                endX = gridViewWidth;
                endY = startY;
                break;
            case SOUTH:
                endX = startX;
                endY = gridViewHeight;
                break;
            case WEST:
                endX = -roundySize;
                endY = startY;
                break;
            case NORTH_EAST:
                if (roundy.getCellIndex() % (Constants.GRID_SIZE - 1) == 0) {
                    Log.d(TAG, "roundy is on SW-NE diagonal");
                    endX = gridViewWidth;
                    endY = -roundySize;
                } else if (startX + startY < gridViewHeight) {
                    Log.d(TAG, "roundy is above SW-NE diagonal");
                    endX = startX + startY + cellSize;
                    endY = -cellSize;
                } else {
                    Log.d(TAG, "roundy is below SW-NE diagonal");
                    endX = gridViewWidth;
                    endY = startY - (gridViewWidth - startX);
                }
                break;
            case SOUTH_WEST:
                if (roundy.getCellIndex() % (Constants.GRID_SIZE - 1) == 0) {
                    Log.d(TAG, "roundy is on SW-NE diagonal");
                    endX = -roundySize;
                    endY = gridViewHeight;
                } else if (startX + startY < gridViewHeight) {
                    Log.d(TAG, "roundy is above SW-NE diagonal");
                    endX = -cellSize;
                    endY = startX + startY + cellSize;
                } else {
                    Log.d(TAG, "roundy is below SW-NE diagonal");
                    endX = gridViewWidth - (gridViewHeight - (startY - (gridViewWidth - startX)));
                    endY = gridViewHeight;
                }
                break;
            case NORTH_WEST:
                if (startX < startY) {
                    Log.d(TAG, "roundy is below NW-SE diagonal");
                    endX = -roundySize;
                    endY = startY - startX - roundySize;
                } else if (startX == startY) {
                    Log.d(TAG, "roundy is on NW-SE diagonal");
                    endX = -roundySize;
                    endY = -roundySize;
                } else {
                    Log.d(TAG, "roundy is above NW-SE diagonal");
                    endX = startX - startY - roundySize;
                    endY = -roundySize;
                }
                break;
            case SOUTH_EAST:
                if (startX < startY) {
                    Log.d(TAG, "roundy is below NW-SE diagonal");
                    endX = startX + gridViewHeight - startY;
                    endY = gridViewHeight;
                } else if (startX == startY) {
                    Log.d(TAG, "roundy is on NW-SE diagonal");
                    endX = gridViewWidth;
                    endY = gridViewHeight;
                } else {
                    Log.d(TAG, "roundy is above NW-SE diagonal");
                    endX = gridViewWidth;
                    endY = startY + gridViewWidth - startX;
                }
                break;
        }

        LogUtility.logMove(view, endX, endY);

        view.animate().x(endX).y(endY).setDuration(Constants.MOVE_SPEED).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onMoveOutAnimationEnd(roundy);
            }
        });
    }
}
