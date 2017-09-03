package gl4di4tor.assets;

/**
 * Created by Gladiator on 7/29/2017 AD.
 */
public enum ErrorCode {
    PERMISSION_DENIED(1, "You need root permission for executing this program.");

    int value;
    String description;

    ErrorCode(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return this.value;
    }

    public String getDescription() {
        return description;
    }
}
