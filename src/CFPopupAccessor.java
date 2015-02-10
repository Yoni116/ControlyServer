import aurelienribon.tweenengine.TweenAccessor;

public class CFPopupAccessor implements TweenAccessor<CFPopup> {

	  // The following lines define the different possible tween types.
    // It's up to you to define what you need :-)

    public static final int POSITION_X = 1;
    public static final int POSITION_Y = 2;
    public static final int POSITION_XY = 3;

    // TweenAccessor implementation

    @Override
    public int getValues(CFPopup target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.getBounds().x; return 1;
            case POSITION_Y: returnValues[0] = target.getBounds().y; return 1;
            case POSITION_XY:
                returnValues[0] = target.getLocation().x;
                returnValues[1] = target.getLocation().y;
                return 2;
            default: assert false; return -1;
        }
    }
    
    @Override
    public void setValues(CFPopup target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: target.setX(newValues[0]); break;
            case POSITION_Y: target.setY(newValues[0]); break;
            case POSITION_XY:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            default: assert false; break;
        }
    }

    
}
