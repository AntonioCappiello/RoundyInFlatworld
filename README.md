# Roundy In Flatworld
This is just an exercise of a simple 2D-game in which Roundies roll over a Flatworld.

## Flatworld
Flatworld is a grid of 8x8 and is inhabited by the “Roundy” specie, a ball like creature that can only move in one direction once they start rolling and cannot stop by themselves. 
They can roll horizontally (keeping the same x coordinate), vertically (keeping the same z coordinate) or diagonally. 

Roundies fall of the world and die if they reach the end of it when moving. 

If Roundy A moves around and hits another Roundy B, this Roundy B will get the impulse to move in the same direction as the Roundy A that hit it.
Roundy A will stop and take B’s square in the Flatworld.

Roundies are very nervous about being hit by other Roundies, they like to stay in squares where they cannot be struck by other Roundies.

## How the game works
- Upon start, the app randomly places 7 Roundy’s in this Flatworld.
- The game start when the user insert an 8th Roundy on a randomly chosen (free) square.
- When all 8 Roundies are in their designated squares, the program will identify the unhappy
Roundies that could potentially be hit by other Roundies. The app will change the color of these
unhappy Roundies to show the user which Roundies are unhappy.
- After the user selects an unhappy Roundy, it will start moving towards one of the Roundies that it
can hit, following the behaviour identified above.

## Notes
In this version of the game extra focus was given to algorithmic efficency and to create clean code.
UI is not important yet. It only serves for now to communicate clearly to the user what is happening in the game.
