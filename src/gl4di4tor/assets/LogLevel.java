package gl4di4tor.assets;

/**
 * Created by Gladiator on 7/28/2017 AD.
 */
public enum LogLevel {
    DEBUG(5),
    INFO(4),
    WARNING(3),
    ERROR(2),
    FATAL(1),
    PERSIST(0);

    private int value;

    LogLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
