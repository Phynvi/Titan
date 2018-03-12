package org.maxgamer.rs.tilus.paths;

import org.maxgamer.rs.tilus.Section;

/**
 * TODO: Document this
 */
public class Move implements Comparable<Move> {
    private Move previous;
    private int cost;
    private int dx;
    private int dy;
    private Coordinate endingCoordinate;
    private Section from;
    private int heuristic;
    private int bestCaseCost;

    public Move(Move previous, int cost, int dx, int dy, Coordinate endingCoordinate, Section from, DestinationBounds destination) {
        this.previous = previous;
        this.cost = cost;
        this.dx = dx;
        this.dy = dy;
        this.endingCoordinate = endingCoordinate;
        this.from = from;

        // TODO: picking end.min isn't quite right for the heuristic
        this.heuristic = endingCoordinate.distanceSq(destination.min);
        this.bestCaseCost = cost + this.heuristic;
    }

    public Move getPrevious() {
        return previous;
    }

    public int getCost() {
        return cost;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Coordinate getEndingCoordinate() {
        return endingCoordinate;
    }

    public Section getFrom() {
        return from;
    }

    @Override
    public int compareTo(Move that) {
        return (this.bestCaseCost) - (that.bestCaseCost);
    }
}
