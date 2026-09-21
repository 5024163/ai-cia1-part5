package astar.model;

/** A message for the user, with a kind that decides its colour. */
public record Status(Kind kind, String message) {
    public enum Kind {
        IDLE, INFO, RUNNING, SUCCESS, ERROR
    }
}
