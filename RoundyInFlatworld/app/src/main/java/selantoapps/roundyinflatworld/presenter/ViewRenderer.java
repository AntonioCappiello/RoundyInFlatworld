package selantoapps.roundyinflatworld.presenter;

import android.view.View;

public interface ViewRenderer {
    void removeView(View view);

    void refresh();

    void enableInput(boolean enable);

    View createRoundyView(int id, int rowIndex, int columnIndex, int bgResId);

    void addView(View roundyView);

    void showToast(int stringResId);

    void showToast(int stringResId, String argument);

    void showAsHappy(boolean happy, View view);
}
