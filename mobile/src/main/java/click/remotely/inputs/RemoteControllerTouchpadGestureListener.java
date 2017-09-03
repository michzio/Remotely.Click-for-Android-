package click.remotely.inputs;

import android.view.MotionEvent;

import click.remotely.android.MainDrawerActivity;
import click.remotely.android.interfaces.RemoteControllerClientInterface;
import click.remotely.android.services.RemoteControllerClientService;

/**
 * Created by michzio on 29/08/2017.
 */

public class RemoteControllerTouchpadGestureListener implements TouchpadGestureDetector.OnGestureListener {

    private RemoteControllerClientInterface mClientInterface;

    public RemoteControllerTouchpadGestureListener( RemoteControllerClientInterface clientInterface) {

        mClientInterface = clientInterface;
    }

    @Override
    public boolean onCursorMove(MotionEvent evt, float dx, float dy) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseMove(dx/4, dy/4); // divide by four to increase accuracy
        }

        return true;
    }

    @Override
    public boolean onSingleTap(MotionEvent evt) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseClick(RemoteControllerClientService.MouseButton.LEFT);
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent evt) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseDoubleClick(RemoteControllerClientService.MouseButton.LEFT);
        }

        return true;
    }

    @Override
    public boolean onTripleTap(MotionEvent evt) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseTripleClick(RemoteControllerClientService.MouseButton.LEFT);
        }

        return true;
    }

    @Override
    public boolean onSingleRightTap(MotionEvent evt) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseClick(RemoteControllerClientService.MouseButton.RIGHT);
        }

        return true;
    }

    @Override
    public boolean onDoubleRightTap(MotionEvent evt) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseDoubleClick(RemoteControllerClientService.MouseButton.RIGHT);
        }

        return true;
    }

    @Override
    public boolean onTripleRightTap(MotionEvent evt) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseTripleClick(RemoteControllerClientService.MouseButton.RIGHT);
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent evt, TouchpadGestureDetector.Direction scrollDirection, float scrollValue) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseScroll(scrollDirection, (double) scrollValue/8);
        }

        return true;
    }

    long pinchTime = System.currentTimeMillis();
    private static final long PINCH_TIME_INTERVAL = 500;

    @Override
    public boolean onPinch(MotionEvent evt, float scaleFactor) {


        if(System.currentTimeMillis() - PINCH_TIME_INTERVAL > pinchTime) {

            pinchTime = System.currentTimeMillis();

            RemoteControllerClientService clientService = mClientInterface.getClientService();
            if (clientService != null) {
                clientService.mousePinch((double) scaleFactor);
            }

            return true;
        } else {
            return false;
        }
    }

    long swipeTime = System.currentTimeMillis();
    private static final long SWIPE_TIME_INTERVAL = 500;

    @Override
    public boolean onTripleSwipe(MotionEvent evt, TouchpadGestureDetector.Direction swipeDirection, float swipeValue) {

        if(System.currentTimeMillis() - SWIPE_TIME_INTERVAL > swipeTime) {

            swipeTime = System.currentTimeMillis();

            RemoteControllerClientService clientService = mClientInterface.getClientService();
            if (clientService != null) {
                clientService.mouseTripleSwipe(swipeDirection, (double) swipeValue);
            }

            return true;
        } else {
            return false;
        }
    }

    long rotateTime = System.currentTimeMillis();
    private static final long ROTATE_TIME_INTERVAL = 500;

    @Override
    public boolean onRotate(MotionEvent evt, float rotationAngle) {

        if(System.currentTimeMillis() - ROTATE_TIME_INTERVAL > rotateTime) {

            rotateTime = System.currentTimeMillis();

            RemoteControllerClientService clientService = mClientInterface.getClientService();
            if (clientService != null) {
                clientService.mouseRotate( (double) rotationAngle);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDrag(MotionEvent evt, float dx, float dy) {

        RemoteControllerClientService clientService = mClientInterface.getClientService();
        if(clientService != null) {
            clientService.mouseDrag(dx/4, dy/4); // divide by four to increase accuracy
        }

        return false;
    }
}
