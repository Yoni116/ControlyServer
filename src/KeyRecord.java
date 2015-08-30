/**
 * Created by yoni on 26/08/2015.
 */
public class KeyRecord {

    private long time;
    private int keyCode;
    private int keyUpDown; // 0 = down, 1 = up

    public KeyRecord(int key, int mode) {
        this.time = 0;
        keyCode = key;
        keyUpDown = mode;
    }

    public KeyRecord(long time, int key, int mode) {
        this.time = time;
        keyCode = key;
        keyUpDown = mode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyUpDown() {
        return keyUpDown;
    }

    public void setKeyUpDown(int keyUpDown) {
        this.keyUpDown = keyUpDown;
    }

    @Override
    public String toString() {
        return "KeyRecord{" +
                "time=" + time +
                ", keyCode=" + keyCode +
                ", keyUpDown=" + keyUpDown +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyRecord keyRecord = (KeyRecord) o;

        if (keyCode != keyRecord.keyCode) return false;
        if (keyUpDown != keyRecord.keyUpDown) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = keyCode;
        result = 31 * result + keyUpDown;
        return result;
    }
}
