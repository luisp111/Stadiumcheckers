package edu.up.cs301.stadiumcheckers.views;

public class SCSurfaceViewThread extends Thread {
    private final SCSurfaceView view;

    public SCSurfaceViewThread(SCSurfaceView view) {
        super();
        this.view = view;
    }

    @Override
    public void run() {
        while (view.shouldKeepUpdating()) {
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            view.postInvalidate();
        }
    }
}
