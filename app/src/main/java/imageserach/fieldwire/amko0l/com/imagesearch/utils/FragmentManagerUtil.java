package imageserach.fieldwire.amko0l.com.imagesearch.utils;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class FragmentManagerUtil {
    public static void createFragment(int id, Fragment pFragment, String tagName, AppCompatActivity context, boolean backStateMaintained) {
        if (isActivityActive(context)) {
            FragmentManager fm = context.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (backStateMaintained) {
                ft.addToBackStack(tagName);
            }
            ft.replace(id, pFragment, tagName);
            ft.commitAllowingStateLoss();
        }
    }

    private static boolean isActivityActive(Activity activity) {
        return !(activity == null || activity.isFinishing() || activity.isDestroyed());
    }
}
