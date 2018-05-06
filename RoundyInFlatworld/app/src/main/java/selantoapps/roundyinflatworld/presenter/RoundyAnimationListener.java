package selantoapps.roundyinflatworld.presenter;

import selantoapps.roundyinflatworld.model.Roundy;
import selantoapps.roundyinflatworld.settings.Direction;

public interface RoundyAnimationListener {
    void onMoveAnimationEnd(Roundy roundyA, Roundy roundyB, Direction direction);

    void onMoveOutAnimationEnd(Roundy roundy);
}
