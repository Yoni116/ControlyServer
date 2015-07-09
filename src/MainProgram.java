/**
 * Created by yoni on 08/07/2015.
 */
public class MainProgram {
    public static void main(String[] args) {

        System.out.println(System.getenv("COMPUTERNAME"));
        System.out.println(System.getProperty("os.name"));

        CFMainFrame frame = new CFMainFrame();
        frame.setFocusable(true);
        frame.setVisible(true);
        frame.requestFocus();
    }
}