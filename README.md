# JavaBship
Small Java-based [Battleship](http://www.hasbro.com/common/instruct/battleship.pdf) game programmed in a few days for a job exercise.

##Compilation
This project is built with [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) and [LibGDX](https://libgdx.badlogicgames.com/download.html), so these will likely be required to build the project from source. Please refer to their respective documentations for [setting up a LibGDX project in IntelliJ](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle), and for opening a project from Git with IntelliJ (Should just be File->New->Project from Version Control->Git).

Once LibGDX and IntelliJ are configured properly, building the project should be straightforward. JavaBship shouldn't require any of the optional LibGDX extensions, so the defaults are fine.

If all else fails, I've uploaded a release where an exported .jar with no external dependencies can be found.

##Playing the game
When the game first launches, it is the player's turn to place ships. Rotate them with the right mouse button and place with the left mouse button. Once all five ships are placed, the game will start. On the player's turn, they guess an enemy ship's location by clicking the left mouse button in an empty square. The enemy AI will then take over and try to guess the location of a player's ship. Turns continue until either the player or the AI sink all ships on their target board.

Other relevant controls for the game:
* F5 - restarts the game
* Escape - quits the game
* S - (cheat code) Shows the locations of the enemy ships while it's the player's turn
* D - Changes the enemy AI difficulty

##Code Design
The game is segmented (as per standard OOP practice) into multiple classes, all of which were designed to perform one simple task and perform it well. Battleship in particular is a good game for object-oriented programming, and as the physical game can be easily segmented into different physical objects, a video game version can also be similarly segmented.

The game's classes are implemented as follows:

Class | Purpose
------|--------
Ship | Abstract base class for ship types. Contains procedures for firing at the ship, tests to see if the ship is sunk, etc
Ship_* | Derived child classes of Ship that implement ship-specific information, such as size, ship name, etc
Board | Base class for board types that manages the ships, draws the background, etc
Board_Player | Derived child class of Board that handles user interaction with the board, such as placing or rotating ships
EnemyAI | Class that handles enemy AI decision-making and where it guesses next
MyBattleshipGame | Game controller class that implements LibGDX functionality, handles game state & user input

These classes and respective files can be found in the subfolder core/src/com/meh2481/battleship/ within the repo.

There is also one more class DesktopLauncher within desktop/src/com/meh2481/battleship/desktop/ that contains the entry point for the program. This class does little except set up the LibGDX environment and start the engine's main loop.

Also note that the AI still can be considered unintelligent even in hard AI mode, as it doesn't take into account ship sizes when guessing at the player's ships. It'll often guess in one-tile gaps or gaps that otherwise could not contain any of the remaining ships.
