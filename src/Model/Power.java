package Model;

public enum Power {
    SPEED(10_000),
    FREEZE(10_000),
    INVISIBILITY(10_000),
    BONUS(0),
    LIFE(0);

    public final int ms;
    Power(int ms) { this.ms = ms; }
}
