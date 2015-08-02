


public class CFClient {

    private String name;
    private String ip;


    public CFClient(String clientName, String clientIP) {
        name = clientName;
        ip = clientIP;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "ClientName: " + name + "\tClientIP: " + ip;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + ip.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.name.equals(((CFClient) obj).name))
            return true;
        else
            return false;

    }
}
