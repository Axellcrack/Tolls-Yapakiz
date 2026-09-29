package com.headshothelper.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

public class TouchAccessibilityService extends AccessibilityService {

    private static TouchAccessibilityService instance;

    public static TouchAccessibilityService getInstance() { return instance; }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent e) {}
    @Override public void onInterrupt() {}

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    public void smoothDrag(float startX, float startY,
                           float dx, float dy,
                           float smooth, int durationMs) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;

        int steps = 8 + (int)(smooth * 20);
        Path path = new Path();
        path.moveTo(startX, startY);

        for (int i = 1; i <= steps; i++) {
            float t = (float) i / steps;
            float ease = 1.0f - (float) Math.pow(1.0f - t, 3.0f);
            path.lineTo(startX + dx * ease, startY + dy * ease);
        }

        GestureDescription.StrokeDescription stroke =
            new GestureDescription.StrokeDescription(path, 0, durationMs);
        GestureDescription.Builder b = new GestureDescription.Builder();
        b.addStroke(stroke);
        dispatchGesture(b.build(), null, null);
    }

    public void tap(float x, float y) {
        Path p = new Path();
        p.moveTo(x, y);
        GestureDescription.StrokeDescription s =
            new GestureDescription.StrokeDescription(p, 0, 30);
        dispatchGesture(new GestureDescription.Builder().addStroke(s).build(),
            null, null);
    }
}
