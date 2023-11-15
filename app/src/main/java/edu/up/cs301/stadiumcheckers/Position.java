package edu.up.cs301.stadiumcheckers;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Stadium Checkers
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 * <p>
 * Class to store the position of a marble.
 */
public class Position {
    // the ring value goes from 0 to (ring count), 0 being the starting ring
    // a ring value is -2 when the ball needs to be reset
    // a ring value is -1 when the ball is secured (players need all 5 secured to win)
    private int ring;
    // the slot the marble occupies on the current ring
    private int slot;

    public Position(int slot) {
        this.ring = 0;
        this.slot = slot;
    }

    public Position(int ring, int slot) {
        this.ring = ring;
        this.slot = slot;
    }

    public int getRing() {
        return ring;
    }

    public int getSlot() {
        return slot;
    }

    public void setPosition(int ring, int slot) {
        this.ring = ring;
        this.slot = slot;
    }

    @NonNull
    @Override
    public String toString() {
        return "[r" + ring + ", s" + slot + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return ring == position.ring && slot == position.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ring, slot);
    }
}
