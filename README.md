<p align="center">
  <img src="banner.png" alt="Pac-Man" width="100%">
</p>

# Pac-Man

A Pac-Man game written in Java with Swing. You move Pac-Man around a maze, eat all the food, collect power-ups, and avoid the ghosts. The maze is generated randomly every time, so no two games look the same. It keeps a high score list and gets harder each level.

I made this project to practice Java, Swing, and working with threads, since the player, the ghosts, and the timer all run at the same time.

## Features

- A new random maze every game (built with a depth-first maze generator).
- You choose the board size at the start.
- Four ghosts that move on their own and chase around the maze.
- Power-ups you get from special tiles: speed boost, freezing the ghosts, becoming invisible, a bonus of points, or an extra life. The timed ones last about 10 seconds.
- Three lives, a score, and a timer.
- When you clear all the food the level is complete and the next maze is a bit bigger.
- A high score list that is saved between games and asks for your name when the game ends.

## How to play

Use the **arrow keys** to move Pac-Man. Eat all the food to finish the level. Grab the upgrade tiles for a random power-up. If a ghost catches you, you lose a life, unless you are invisible at that moment. The game ends when you run out of lives, and then you can save your score.

## Images (important)

The game needs a `resources` folder with all of its images: the Pac-Man sprites for each direction, the closed-mouth sprite, the four ghosts, food, wall, path, the upgrade tile, and `poster.png` for the menu. These are loaded from the classpath, so in IntelliJ the `resources` folder has to be marked as a **Resources Root**. If the images are missing, the game won't start.

## High scores

Your scores are saved to a file called `.pacman-scores` in your home folder. This file is created automatically the first time you save a score, so you don't need to add it to the project.

## Requirements and running

- Java 17 or newer.
- The easiest way to run it is to open the project in IntelliJ IDEA, make sure the source folder is set as the sources root and the `resources` folder as the resources root, and run `Main`.
