package top.srsea.capture.core.vpn;

public class VpnEvent {
    private OnPacketListener onPacketListener;
    private OnStartListener onStartListener;
    private OnStopListener onStopListener;

    public static VpnEvent getInstance() {
        return Singleton.instance;
    }

    public void setOnStartListener(OnStartListener onStartListener) {
        this.onStartListener = onStartListener;
    }

    public void setOnStopListener(OnStopListener onStopListener) {
        this.onStopListener = onStopListener;
    }

    public void setOnPacketListener(OnPacketListener onPacketListener) {
        this.onPacketListener = onPacketListener;
    }

    public void notifyReceive() {
        if (onPacketListener != null) onPacketListener.onReceive();
    }

    public void notifyStart() {
        if (onStartListener != null) onStartListener.onStart();
    }

    public void notifyStop() {
        if (onStopListener != null) onStopListener.onStop();
    }

    public void cancelAll() {
        onPacketListener = null;
        onStartListener = null;
        onStopListener = null;
    }

    public interface OnPacketListener {
        void onReceive();
    }

    public interface OnStartListener {
        void onStart();
    }

    public interface OnStopListener {
        void onStop();
    }

    private static class Singleton {
        static VpnEvent instance = new VpnEvent();
    }
}
