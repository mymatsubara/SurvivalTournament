package io.github.mymatsubara.survivaltournament.models;

public class Point {
    public int x;
    public int z;

    public Point(int x, int z) {
        this.x = x;
        this.z = z;
    }
    public Point() {
        this(0, 0);
    }
}
