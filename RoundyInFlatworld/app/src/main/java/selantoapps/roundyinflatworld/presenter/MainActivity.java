package selantoapps.roundyinflatworld.presenter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import selantoapps.roundyinflatworld.R;
import selantoapps.roundyinflatworld.controller.CollisionDetector;
import selantoapps.roundyinflatworld.controller.GameEngine;
import selantoapps.roundyinflatworld.model.Roundy;
import selantoapps.roundyinflatworld.settings.Constants;
import selantoapps.roundyinflatworld.settings.Direction;
import selantoapps.roundyinflatworld.widget.SquareLinearLayout;

public class MainActivity extends AppCompatActivity implements ViewRenderer {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.rootView)
    RelativeLayout rootView;

    @BindView(R.id.gridView)
    SquareLinearLayout gridView;

    @BindView(R.id.addRoundyBtn)
    AppCompatButton addRoundyBtn;

    @BindColor(R.color.white)
    int white;

    @BindDrawable(R.drawable.circle_solid_red)
    Drawable unHappyBg;

    @BindDrawable(R.drawable.circle_solid_green)
    Drawable happyBg;

    @BindDrawable(R.drawable.circle_solid_red_pressed)
    Drawable onTheMoveBg;

    private int cellSize;
    private int roundySize;
    private int cellPadding;
    private GameEngine gameEngine;
    private Toast toast;
    private RoundyAnimationListener roundyAnimationListener;
    private RoundyViewAnimator roundyViewAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        roundyAnimationListener = new RoundyAnimationListener() {
            @Override
            public void onMoveAnimationEnd(Roundy roundyA, Roundy roundyB, Direction direction) {
                gameEngine.onMoveAnimationEnd(roundyA, roundyB, direction);
            }

            @Override
            public void onMoveOutAnimationEnd(Roundy roundy) {
                gameEngine.onMoveOutAnimationEnd(roundy);
            }
        };

        roundyViewAnimator = new RoundyViewAnimator(onTheMoveBg, roundyAnimationListener);

        gameEngine = new GameEngine(this, new CollisionDetector(Constants.GRID_SIZE),
                roundyViewAnimator, Constants.GRID_SIZE, Constants.ROUNDY_COUNT);

        // inflate the grid
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < Constants.GRID_SIZE; i++) {
            inflater.inflate(R.layout.grid_row, gridView, true);
        }

        gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                cellSize = gridView.getMeasuredHeight() / 8;
                cellPadding = cellSize / 8;
                roundySize = cellSize - cellPadding * 2;

                roundyViewAnimator.init(roundySize, gridView.getWidth(), gridView.getHeight(), cellSize);

                Log.v(TAG, "gridView width: " + gridView.getMeasuredWidth() + " height: " +
                        gridView.getMeasuredHeight() + " cellSize: " + cellSize);

                gameEngine.init();
            }
        });
    }

    @OnClick(R.id.restartBtn)
    public void onRestartBtnClick() {
        gameEngine.restart();
    }

    @OnClick(R.id.addRoundyBtn)
    public void onAddRoundyBtnClick() {
        if (addRoundyBtn.isEnabled()) {
            addRoundyBtn.setEnabled(false);
            gameEngine.addRoundyWithId(Constants.ROUNDY_COUNT - 1);
        }
    }

    private View createRoundyView(int index, int cellSize, int roundySize, int rowIndex, int columnIndex, int margin, int bgResId) {
        int marginLef = cellSize * columnIndex + margin;
        int marginTop = cellSize * rowIndex + margin;

        TextView roundyView = new TextView(this);
        roundyView.setText(String.valueOf(index));
        roundyView.setTextColor(white);
        roundyView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(roundySize, roundySize);
        params.setMargins(marginLef, marginTop, margin, margin);
        roundyView.setLayoutParams(params);
        roundyView.setBackground(ContextCompat.getDrawable(this, bgResId));
        return roundyView;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void removeView(View view) {
        rootView.removeView(view);
    }

    @Override
    public void refresh() {
        gridView.refreshDrawableState();
    }

    @Override
    public void enableInput(boolean enable) {
        addRoundyBtn.setEnabled(true);
    }

    @Override
    public View createRoundyView(int id, int rowIndex, int columnIndex, int bgResId) {
        return createRoundyView(id, cellSize, roundySize, rowIndex, columnIndex, cellPadding, bgResId);
    }

    @Override
    public void addView(View roundyView) {
        rootView.addView(roundyView);
    }

    @Override
    public void showToast(int stringResId) {
        hideExistingToast();
        toast = Toast.makeText(MainActivity.this, stringResId, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void showToast(int stringResId, String argument) {
        hideExistingToast();
        toast = Toast.makeText(MainActivity.this, getString(stringResId, argument), Toast.LENGTH_SHORT);
        toast.show();
    }

    private void hideExistingToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override
    public void showAsHappy(boolean happy, View view) {
        view.setBackground(happy ? happyBg : unHappyBg);
    }
}
