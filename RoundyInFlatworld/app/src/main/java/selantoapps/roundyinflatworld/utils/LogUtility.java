package selantoapps.roundyinflatworld.utils;

import android.util.Log;
import android.view.View;

import selantoapps.roundyinflatworld.model.Roundy;

public class LogUtility {

    private static final String TAG = LogUtility.class.getSimpleName();

    public static void logCollisions(String tag, Roundy[] roundies) {
        StringBuilder collisionsLog = new StringBuilder();
        for (Roundy roundy : roundies) {
            if (roundy == null) {
                continue;
            }
            for (int collidesWithIndex : roundy.getCollisions().keySet()) {
                collisionsLog.append(roundy.getId())
                        .append(" collides with ")
                        .append(collidesWithIndex)
                        .append(" towards ")
                        .append(roundy.getCollisions().get(collidesWithIndex).name())
                        .append("\n");
            }
        }
        Log.d(tag + " - " + TAG, collisionsLog.toString());
    }

    public static void logViewPosition(View view) {
        Log.d(TAG, "logViewPosition");
        Log.d(TAG, "x " + view.getX() + " left " + view.getLeft()
                + " y " + view.getY() + " top " + view.getTop());
    }

    public static void logMove(View viewA, View viewB) {
        logMove(viewA, viewB.getX(), viewB.getY());
    }

    public static void logMove(View view, float endX, float endY) {
        Log.d(TAG, "moving x: " + view.getX() + " -> " + endX + ", y: " + view.getY() + " -> " + endY);
    }
}
