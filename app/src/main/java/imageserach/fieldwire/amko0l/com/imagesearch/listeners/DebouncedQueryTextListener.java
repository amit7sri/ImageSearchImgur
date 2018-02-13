package imageserach.fieldwire.amko0l.com.imagesearch.listeners;

import android.os.SystemClock;
import android.support.v7.widget.SearchView;
import android.util.Log;

/**
 * Created by amit7 on 2/13/2018.
 */

public abstract class DebouncedQueryTextListener implements SearchView.OnQueryTextListener {

    private static final String TAG = DebouncedQueryTextListener.class.getSimpleName();
    private static final long THRESHOLD_MILLIS = 1000L;
    private long lastClickMillis;
    private int calls = 0;

    protected DebouncedQueryTextListener() {

    }

    public abstract void onQueryDebounce(String text);

    @Override
    public boolean onQueryTextChange(String newText) {
        long now = SystemClock.elapsedRealtime();
        if (now - lastClickMillis > THRESHOLD_MILLIS) {
            calls += 1;
            onQueryDebounce(newText);
            Log.d(TAG, "onQueryTextChange: " + calls);
        }
        lastClickMillis = now;
        return true;
    }
}