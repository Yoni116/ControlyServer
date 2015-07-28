import aurelienribon.tweenengine.TweenAccessor;

import javax.swing.*;

/**
 * Created by yoni on 26/07/2015.
 */
public class FrameAnimationAccessor implements TweenAccessor<JFrame> {

    public static final int WIDTH_ANIMATION = 1;
    public static final int HEIGHT_ANIMATION = 2;
    public static final int BOTH_ANIMATION = 3;


    @Override
    public int getValues(JFrame target, int type, float[] values) {
        switch (type) {
            case WIDTH_ANIMATION:
                values[0] = (float) target.getWidth();
                return 1;
            case HEIGHT_ANIMATION:
                values[0] = (float) target.getBounds().getHeight();
                return 1;
            case BOTH_ANIMATION:
                values[0] = (float) target.getBounds().getWidth();
                values[1] = (float) target.getBounds().getHeight();
                return 2;
            default:
                return -1;
        }

    }

    @Override
    public void setValues(JFrame target, int type, float[] values) {
        switch (type) {
            case WIDTH_ANIMATION:
                target.setSize((int) values[0], (int) target.getBounds().getHeight());
                break;
            case HEIGHT_ANIMATION:
                target.setSize((int) target.getBounds().getWidth(), (int) values[0]);
                break;
            case BOTH_ANIMATION:
                target.setSize((int) values[0], (int) values[1]);
                break;
            default:
                break;
        }

    }
}
