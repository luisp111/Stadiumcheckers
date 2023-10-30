package edu.up.cs301.stadiumcheckers;

import java.util.HashMap;

/**
 * Stadium Checkers
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 */

/**
 * Class for being able to embed hashmaps into arrays
 */
public class SlotMap {
    private HashMap<Integer, Integer> map;

    public HashMap<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, Integer> map) {
        this.map = map;
    }
}
