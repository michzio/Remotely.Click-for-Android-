package click.remotely.android.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViewsService;

import click.remotely.android.R;
import click.remotely.android.RemoteDevicesActivity;
import click.remotely.inputs.TouchpadGestureDetector;
import click.remotely.model.DeviceInfo;
import click.remotely.networking.ClientInfo;
import click.remotely.networking.RemoteControllerClient;

public class RemoteControllerClientService extends Service {

    private static final String TAG = RemoteViewsService.class.getName();
    private static final String DEFAULT_CLIENT_IDENTITY = "Android Phone";
    private static final String DEFAULT_REMOTE_DEVICE_NAME = "Unknown Device";

    public static final String EXTRA_CONNECTION_IP = "RemoteControllerClientService.EXTRA_CONNECTION_IP";
    public static final String EXTRA_CONNECTION_PORT = "RemoteControllerClientService.EXTRA_CONNECTION_PORT";
    public static final String EXTRA_SECURITY_PASSWORD = "RemoteControllerClientService.EXTRA_SECURITY_PORT";
    public static final String EXTRA_CLIENT_IDENTITY = "RemoteControllerClientService.EXTRA_CLIENT_IDENTITY";
    public static final String EXTRA_REMOTE_DEVICE_NAME = "RemoteControllerClientService.EXTRA_REMOTE_DEVICE_NAME";
    public static final String EXTRA_REMOTE_DEVICE_TYPE = "RemoteControllerClientService.EXTRA_REMOTE_DEVICE_TYPE";

    public static final int ONGOING_NOTIFICATION_ID = 0x01;

    private final IBinder mBinder = new LocalBinder();
    private ClientInfo mClientInfo  = new ClientInfo();
    private String mRemoteDeviceName;
    private DeviceInfo.Type mRemoteDeviceType;
    private RemoteControllerClient rc_client = new RemoteControllerClient();

    private boolean mClientStartedFlag = false;

    public RemoteControllerClientService() { }

    @Override
    public void onCreate() {
        super.onCreate();

        // set basic (default) data on client info
        mClientInfo.setPasvPort(ClientInfo.CLIENT_PORT_ANY);
        mClientInfo.setClientOS(ClientInfo.CLIENT_OS);

        // set client events callbacks on client info
        mClientInfo.setConnectionStartListener( (int sockFd, Object callbackArg) -> onConnectionStart(sockFd, callbackArg) );
        mClientInfo.setConnectionAuthenticatedListener( (int sockFd, Object callbackArg) ->  onConnectionAuthenticated(sockFd, callbackArg) );
        mClientInfo.setConnectionEndListener( (int sockFd, Object callbackArg) -> onConnectionEnd(sockFd, callbackArg) );
        mClientInfo.setConnectionErrorListener( (int sockFd, int errorCode, String errorMsg, Object callbackArg) -> onConnectionError(sockFd, errorCode, errorMsg, callbackArg) );
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!mClientStartedFlag) {

            mRemoteDeviceName = intent.getExtras().getString(EXTRA_REMOTE_DEVICE_NAME, DEFAULT_REMOTE_DEVICE_NAME);
            mRemoteDeviceType = (DeviceInfo.Type) intent.getExtras().get(EXTRA_REMOTE_DEVICE_TYPE);

            String connIp = intent.getExtras().getString(EXTRA_CONNECTION_IP);
            String connPort = intent.getExtras().getString(EXTRA_CONNECTION_PORT);
            String securityPassword = intent.getExtras().getString(EXTRA_SECURITY_PASSWORD, "");
            String clientIdentity = intent.getExtras().getString(EXTRA_CLIENT_IDENTITY, DEFAULT_CLIENT_IDENTITY);

            // set connection data on client info
            mClientInfo.setConnIp(connIp);
            mClientInfo.setConnPort(connPort);
            mClientInfo.setSecurityPassword(securityPassword);
            mClientInfo.setClientIdentity(clientIdentity);

            startConnection();
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public RemoteControllerClientService getService() {
            return RemoteControllerClientService.this;
        }
    }

    public void startConnection() {

        Thread server_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                rc_client.connect(mClientInfo);
            }
        });
        server_thread.start();

        mClientStartedFlag = true;
    }

    public void stopConnection() {

        rc_client.disconnect(mClientInfo);
    }

    private void onConnectionStart(int sockFd, Object callbackArg) {

        Log.d(TAG, "On connection started on socket: " + sockFd);
    }

    private void onConnectionAuthenticated(int sockFd, Object callbackArg) {

        Log.d(TAG, "On connection authenticated on socket: " + sockFd);
        mClientInfo.setSockFd(sockFd);
        notifyRemoteConnectionEstablished();
    }

    private void onConnectionEnd(int sockFd, Object callbackArg) {

        Log.d(TAG, "On connection end on socket: " + sockFd);
        mClientInfo = null;
        mClientStartedFlag = false;

        stopSelf();
    };

    private void onConnectionError(int sockFd, int errorCode, String errorMsg, Object callbackArg) {

        Log.d(TAG, "On connection error on socket: " + sockFd + " with message: (" + errorCode + ") " + errorMsg);
        mClientInfo = null;
        mClientStartedFlag = false;

        stopSelf();
    }

    private void notifyRemoteConnectionEstablished() {

        Intent notificationIntent = new Intent(this, RemoteDevicesActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String notificationTitle = String.format(getString(R.string.remote_controller_client_service_notification_title), mRemoteDeviceName);
        String notificationTicker = String.format(getString(R.string.remote_controller_client_service_notification_ticker), mRemoteDeviceName);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(getText(R.string.remote_controller_client_service_notification_text))
                .setSmallIcon(R.drawable.ic_remote_devices_white_24dp)
                .setContentIntent(pendingIntent)
                .setTicker(notificationTicker)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceInfo.service.getClassName().equals(RemoteControllerClientService.class.getName())) {
                return true;
            }
        }
        return false;
    }

    public String getRemoteDeviceName() {
        if(mClientInfo == null) return null;
        return mRemoteDeviceName;
    }

    public String getRemoteDeviceIp() {
        if(mClientInfo == null) return null;
        return mClientInfo.getConnIp();
    }

    public String getRemoteDevicePort() {
        if(mClientInfo == null) return null;
        return mClientInfo.getConnPort();
    }

    public DeviceInfo.Type getRemoteDeviceType() {
        if(mClientInfo == null) return null;
        return mRemoteDeviceType;
    }


    /**
     * Remote Controller Client Service - mouse, touchpad, keyboard, app shortcuts interface
     */

    // keyboard input constants
    private static final String REQ_KEYBOARD_INPUT = "keyboard input: %s modifier flags: %s";
    // mouse input constants
    private static final String REQ_MOUSE_MOVE = "mouse move by: %.2f %.2f";
    private static final String REQ_MOUSE_DRAG = "mouse drag by: %.2f %.2f";
    private static final String REQ_MOUSE_CLICK = "mouse click: %s";
    private static final String REQ_MOUSE_DOUBLE_CLICK = "mouse double click: %s";
    private static final String REQ_MOUSE_TRIPLE_CLICK = "mouse triple click: %s";
    private static final String REQ_MOUSE_SCROLL = "mouse scroll: %.2f direction: %s";
    private static final String REQ_MOUSE_PINCH = "mouse pinch: %.2f";
    private static final String REQ_MOUSE_TRIPLE_SWIPE = "mouse triple swipe: %.2f direction: %s";
    private static final String REQ_MOUSE_ROTATE = "mouse rotate: %.2f";
    // player event constants
    private static final String REQ_PLAYER_VOLUME_MUTE = "player: volume mute";
    private static final String REQ_PLAYER_VOLUME_DOWN = "player: volume down";
    private static final String REQ_PLAYER_VOLUME_UP = "player: volume up";
    private static final String REQ_PLAYER_PLAY_PAUSE = "player: play";
    private static final String REQ_PLAYER_STOP = "player: stop";
    private static final String REQ_PLAYER_STEP_FORWARD = "player: step forward";
    private static final String REQ_PLAYER_STEP_BACKWARD = "player: step backward";
    private static final String REQ_PLAYER_SKIP_NEXT = "player: skip next";
    private static final String REQ_PLAYER_SKIP_PREVIOUS = "player: skip previous";
    private static final String REQ_PLAYER_LOOP = "player: loop";
    private static final String REQ_PLAYER_SHUFFLE = "player: shuffle";
    private static final String REQ_PLAYER_SUBTITLES = "player: subtitles";
    private static final String REQ_PLAYER_FULLSCREEN = "player: fullscreen";
    // slide show event constants
    private static final String REQ_SLIDE_SHOW_START = "slide show: start";
    private static final String REQ_SLIDE_SHOW_END = "slide show: end";
    private static final String REQ_SLIDE_SHOW_PREVIOUS = "slide show: previous animated: %s";
    private static final String REQ_SLIDE_SHOW_NEXT = "slide show: next animated: %s";
    private static final String REQ_SLIDE_SHOW_POINTER = "slide show: pointer type: %s";
    private static final String REQ_SLIDE_SHOW_BLANK_SLIDE = "slide show: blank slide type: %s";
    private static final String REQ_SLIDE_SHOW_FIRST_SLIDE = "slide show: first slide";
    private static final String REQ_SLIDE_SHOW_LAST_SLIDE = "slide show: last slide";
    // browser event constants
    private static final String REQ_BROWSER_NEW_TAB = "browser: new tab";
    private static final String REQ_BROWSER_PREVIOUS_TAB = "browser: previous tab";
    private static final String REQ_BROWSER_NEXT_TAB = "browser: next tab";
    private static final String REQ_BROWSER_CLOSE_TAB = "browser: close tab";
    private static final String REQ_BROWSER_OPEN_FILE = "browser: open file";
    private static final String REQ_BROWSER_NEW_PRIVATE_WINDOW = "browser: new private window";
    private static final String REQ_BROWSER_REOPEN_CLOSED_TAB = "browser: reopen closed tab";
    private static final String REQ_BROWSER_CLOSE_WINDOW = "browser: close window";
    private static final String REQ_BROWSER_SHOW_DOWNLOADS = "browser: show downloads";
    private static final String REQ_BROWSER_SHOW_HISTORY = "browser: show history";
    private static final String REQ_BROWSER_SHOW_SIDEBAR = "browser: show sidebar";
    private static final String REQ_BROWSER_SHOW_PAGE_SOURCE = "browser: show page source";
    private static final String REQ_BROWSER_HOME_PAGE = "browser: home page";
    private static final String REQ_BROWSER_RELOAD_PAGE = "browser: reload page";
    private static final String REQ_BROWSER_BOOKMARK_PAGE = "browser: bookmark page";
    private static final String REQ_BROWSER_ENTER_FULL_SCREEN = "browser: enter full screen";
    private static final String REQ_BROWSER_ZOOM_OUT = "browser: zoom out";
    private static final String REQ_BROWSER_ZOOM_ACTUAL_SIZE = "browser: zoom actual size";
    private static final String REQ_BROWSER_ZOOM_IN = "browser: zoom in";
    private static final String REQ_BROWSER_ENTER_LOCATION = "browser: enter location";

    // system event constants
    private static final String REQ_SYSTEM_VOLUME_MUTE = "system: volume mute";
    private static final String REQ_SYSTEM_VOLUME = "system: volume level: %d";
    private static final String REQ_SYSTEM_SHUT_DOWN = "system: shut down";
    private static final String REQ_SYSTEM_RESTART = "system: restart";
    private static final String REQ_SYSTEM_SLEEP = "system: sleep";
    private static final String REQ_SYSTEM_LOG_OUT = "system: log out";

    // shortcut event constants
    private static final String REQ_SHORTCUT_SELECT_ALL = "shortcut: select all";
    private static final String REQ_SHORTCUT_CUT = "shortcut: cut";
    private static final String REQ_SHORTCUT_COPY = "shortcut: copy";
    private static final String REQ_SHORTCUT_PASTE = "shortcut: paste";
    private static final String REQ_SHORTCUT_OPEN_FILE = "shortcut: open file";
    private static final String REQ_SHORTCUT_SAVE = "shortcut: save";
    private static final String REQ_SHORTCUT_FIND = "shortcut: find";
    private static final String REQ_SHORTCUT_PRINT = "shortcut: print";
    private static final String REQ_SHORTCUT_NEW_WINDOW = "shortcut: new window";
    private static final String REQ_SHORTCUT_MINIMIZE_WINDOW = "shortcut: minimize window";
    private static final String REQ_SHORTCUT_CLOSE_WINDOW = "shortcut: close window";
    private static final String REQ_SHORTCUT_SWITCH_APPS = "shortcut: switch apps";
    private static final String REQ_SHORTCUT_UNDO = "shortcut: undo";
    private static final String REQ_SHORTCUT_REDO = "shortcut: redo";
    private static final String REQ_SHORTCUT_SYSTEM_SEARCH= "shortcut: system search";
    private static final String REQ_SHORTCUT_FORCE_QUIT= "shortcut: force quit";
    private static final String REQ_SHORTCUT_SHOW_DESKTOP= "shortcut: show desktop";
    private static final String REQ_SHORTCUT_LEFT_DESKTOP= "shortcut: left desktop";
    private static final String REQ_SHORTCUT_RIGHT_DESKTOP= "shortcut: right desktop";

    public static final String SLIDE_SHOW_POINTER_ARROW = "arrow";
    public static final String SLIDE_SHOW_POINTER_PEN = "pen";
    public static final String SLIDE_SHOW_BLANK_SLIDE_BLACK = "black";
    public static final String SLIDE_SHOW_BLANK_SLIDE_WHITE = "white";

    public static enum MouseButton {
        LEFT,
        RIGHT
    }

    public void sendRawMessage(String message) {
        rc_client.sendMessage(mClientInfo, message);
    }

    public void keyboardInput(String virtualKeyName, String modifierFlagsNames) {

        if(modifierFlagsNames == null) modifierFlagsNames = "";
        String keyboardInputMessage =
                String.format(REQ_KEYBOARD_INPUT, virtualKeyName, modifierFlagsNames);

        // Log.d(TAG, "RPC message <" + keyboardInputMessage + ">.");

       rc_client.sendMessage(mClientInfo, keyboardInputMessage);
    }

    public void mouseMove(double dx, double dy) {

        String mouseMoveMessage = String.format(REQ_MOUSE_MOVE,  dx, dy);

        // Log.d(TAG, "RPC message <" + mouseMoveMessage + ">.");

        rc_client.sendMessage(mClientInfo, mouseMoveMessage);
    }

    public void mouseDrag(double dx, double dy) {

        String mouseDragMessage = String.format(REQ_MOUSE_DRAG,  dx, dy);

        // Log.d(TAG, "RPC message <" + mouseDragMessage + ">.");

        rc_client.sendMessage(mClientInfo, mouseDragMessage);
    }

    public void mouseClick(MouseButton mouseButton) {

        String mouseClickMessage = null;

        switch (mouseButton) {
            case LEFT:
                mouseClickMessage = String.format(REQ_MOUSE_CLICK, "left");
                break;
            case RIGHT:
                mouseClickMessage = String.format(REQ_MOUSE_CLICK, "right");
                break;
        }

        if(mouseClickMessage != null) rc_client.sendMessage(mClientInfo, mouseClickMessage);
    }

    public void mouseDoubleClick(MouseButton mouseButton) {

        String mouseDoubleClickMessage = null;

        switch (mouseButton) {
            case LEFT:
                mouseDoubleClickMessage = String.format(REQ_MOUSE_DOUBLE_CLICK, "left");
                break;
            case RIGHT:
                mouseDoubleClickMessage = String.format(REQ_MOUSE_DOUBLE_CLICK, "right");
                break;
        }

        if(mouseDoubleClickMessage != null) rc_client.sendMessage(mClientInfo, mouseDoubleClickMessage);
    }

    public void mouseTripleClick(MouseButton mouseButton) {

        String mouseTripleClickMessage = null;

        switch (mouseButton) {
            case LEFT:
                mouseTripleClickMessage = String.format(REQ_MOUSE_TRIPLE_CLICK, "left");
                break;
            case RIGHT:
                mouseTripleClickMessage = String.format(REQ_MOUSE_TRIPLE_CLICK, "right");
                break;
        }

        if(mouseTripleClickMessage != null) rc_client.sendMessage(mClientInfo, mouseTripleClickMessage);
    }

    public void mouseScroll(TouchpadGestureDetector.Direction scrollDirection, double scrollValue) {

        String mouseScrollMessage = null;

        switch (scrollDirection) {
            case VERTICAL:
                mouseScrollMessage = String.format(REQ_MOUSE_SCROLL, scrollValue, "vertical");
                break;
            case HORIZONTAL:
                mouseScrollMessage = String.format(REQ_MOUSE_SCROLL, scrollValue, "horizontal");
                break;
        }

        if(mouseScrollMessage != null) rc_client.sendMessage(mClientInfo, mouseScrollMessage);
    }

    public void mousePinch(double scaleFactor) {

        String mousePinchMessage = String.format(REQ_MOUSE_PINCH, scaleFactor);
        rc_client.sendMessage(mClientInfo, mousePinchMessage);
    }

    public void mouseTripleSwipe(TouchpadGestureDetector.Direction swipeDirection, double swipeValue) {

        String mouseTripleSwipeMessage = null;

        switch (swipeDirection) {
            case VERTICAL:
                mouseTripleSwipeMessage = String.format(REQ_MOUSE_TRIPLE_SWIPE, swipeValue, "vertical");
                break;
            case HORIZONTAL:
                mouseTripleSwipeMessage = String.format(REQ_MOUSE_TRIPLE_SWIPE, swipeValue, "horizontal");
                break;
        }

        rc_client.sendMessage(mClientInfo, mouseTripleSwipeMessage);
    }

    public void mouseRotate(double rotationAngle) {

        String mouseRotateMessage = String.format(REQ_MOUSE_ROTATE, rotationAngle);
        rc_client.sendMessage(mClientInfo, mouseRotateMessage);
    }

    public void playerVolumeMute() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_VOLUME_MUTE);
    }

    public void playerVolumeDown() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_VOLUME_DOWN);
    }

    public void playerVolumeUp() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_VOLUME_UP);
    }

    public void playerPlayPause() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_PLAY_PAUSE);
    }

    public void playerStop() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_STOP);
    }

    public void playerStepForward() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_STEP_FORWARD);
    }

    public void playerStepBackward() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_STEP_BACKWARD);
    }

    public void playerSkipNext() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_SKIP_NEXT);
    }

    public void playerSkipPrevious() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_SKIP_PREVIOUS);
    }

    public void playerLoop() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_LOOP);
    }

    public void playerShuffle() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_SHUFFLE);
    }

    public void playerFullScreen() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_FULLSCREEN);
    }

    public void playerSubtitles() {

        rc_client.sendMessage(mClientInfo, REQ_PLAYER_SUBTITLES);
    }

    public void slideShowStart() {

        rc_client.sendMessage(mClientInfo, REQ_SLIDE_SHOW_START);
    }

    public void slideShowEnd() {

        rc_client.sendMessage(mClientInfo, REQ_SLIDE_SHOW_END);
    }

    public void slideShowPrevious(boolean animated) {

        String slideShowPreviousMessage = String.format(REQ_SLIDE_SHOW_PREVIOUS, (animated ? "yes" : "no") );
        rc_client.sendMessage(mClientInfo, slideShowPreviousMessage);
    }

    public void slideShowNext(boolean animated) {

        String slideShowNextMessage = String.format(REQ_SLIDE_SHOW_NEXT, (animated ? "yes" : "no") );
        rc_client.sendMessage(mClientInfo, slideShowNextMessage);
    }

    public void slideShowPointer(String type) {

        String slideShowPointerMessage = String.format(REQ_SLIDE_SHOW_POINTER, type);
        rc_client.sendMessage(mClientInfo, slideShowPointerMessage);
    }

    public void slideShowBlankSlide(String type) {

        String slideShowBlankSlideMessage = String.format(REQ_SLIDE_SHOW_BLANK_SLIDE, type);
        rc_client.sendMessage(mClientInfo, slideShowBlankSlideMessage);
    }

    public void slideShowFirstSlide() {

        rc_client.sendMessage(mClientInfo, REQ_SLIDE_SHOW_FIRST_SLIDE);
    }

    public void slideShowLastSlide() {

        rc_client.sendMessage(mClientInfo, REQ_SLIDE_SHOW_LAST_SLIDE);
    }

    public void browserNewTab() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_NEW_TAB);
    }

    public void browserPreviousTab() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_PREVIOUS_TAB);
    }

    public void browserNextTab() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_NEXT_TAB);
    }

    public void browserCloseTab() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_CLOSE_TAB);
    }

    public void browserOpenFile() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_OPEN_FILE);
    }

    public void browserNewPrivateWindow() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_NEW_PRIVATE_WINDOW);
    }

    public void browserReopenClosedTab() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_REOPEN_CLOSED_TAB);
    }

    public void browserCloseWindow() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_CLOSE_WINDOW);
    }

    public void browserShowDownloads() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_SHOW_DOWNLOADS);
    }

    public void browserShowHistory() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_SHOW_HISTORY);
    }

    public void browserShowSidebar() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_SHOW_SIDEBAR);
    }

    public void browserShowPageSource() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_SHOW_PAGE_SOURCE);
    }

    public void browserHomePage() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_HOME_PAGE);
    }

    public void browserReloadPage() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_RELOAD_PAGE);
    }

    public void browserBookmarkPage() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_BOOKMARK_PAGE);
    }

    public void browserEnterFullscreen() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_ENTER_FULL_SCREEN);
    }

    public void browserZoomOut() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_ZOOM_OUT);
    }

    public void browserZoomActualSize() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_ZOOM_ACTUAL_SIZE);
    }

    public void browserZoomIn() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_ZOOM_IN);
    }

    public void browserEnterLocation() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_ENTER_LOCATION);
    }

    public void systemVolumeMute() {

        rc_client.sendMessage(mClientInfo, REQ_SYSTEM_VOLUME_MUTE);
    }

    public void systemVolumeMax() {
        systemVolume(100);
    }

    public void systemVolume(int volume) {

        String systemVolumeMessage = String.format(REQ_SYSTEM_VOLUME, volume);
        rc_client.sendMessage(mClientInfo, systemVolumeMessage);
    }

    public void systemShutDown() {

        rc_client.sendMessage(mClientInfo, REQ_SYSTEM_SHUT_DOWN);
    }

    public void systemRestart() {

        rc_client.sendMessage(mClientInfo, REQ_SYSTEM_RESTART);
    }

    public void systemSleep() {

        rc_client.sendMessage(mClientInfo, REQ_SYSTEM_SLEEP);
    }

    public void systemLogOut() {

        rc_client.sendMessage(mClientInfo, REQ_SYSTEM_LOG_OUT);
    }

    public void shortcutSelectAll() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_SELECT_ALL);
    }

    public void shortcutCut() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_CUT);
    }

    public void shortcutCopy() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_COPY);
    }

    public void shortcutPaste() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_PASTE);
    }

    public void shortcutOpenFile() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_OPEN_FILE);
    }

    public void shortcutSave() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_SAVE);
    }

    public void shortcutFind() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_FIND);
    }

    public void shortcutPrint() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_PRINT);
    }

    public void shortcutNewWindow() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_NEW_WINDOW);
    }

    public void shortcutMinimizeWindow() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_MINIMIZE_WINDOW);
    }

    public void shortcutCloseWindow() {

        rc_client.sendMessage(mClientInfo, REQ_BROWSER_CLOSE_WINDOW);
    }

    public void shortcutSwitchApps() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_SWITCH_APPS);
    }

    public void shortcutUndo() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_UNDO);
    }

    public void shortcutRedo() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_REDO);
    }

    public void shortcutSystemSearch() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_SYSTEM_SEARCH);
    }

    public void shortcutForceQuit() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_FORCE_QUIT);
    }

    public void shortcutShowDesktop() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_SHOW_DESKTOP);
    }

    public void shortcutLeftDesktop() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_LEFT_DESKTOP);
    }

    public void shortcutRightDesktop() {

        rc_client.sendMessage(mClientInfo, REQ_SHORTCUT_RIGHT_DESKTOP);
    }
}
