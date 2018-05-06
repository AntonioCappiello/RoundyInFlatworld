package selantoapps.roundyinflatworld.controller;

import selantoapps.roundyinflatworld.model.Roundy;

interface CollisionListener {
    void onCollisionFound(Roundy roundyA, Roundy roundyB);
}
