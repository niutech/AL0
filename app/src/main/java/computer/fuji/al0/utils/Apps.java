package computer.fuji.al0.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import computer.fuji.al0.models.ListItem;

public class Apps {
    private static int changedPackagesSequenceNumber = 0;
    private static ArrayList<ListItem> cachedAppsList = new ArrayList<>();

    public static void clearCachedAppsList () {
        if (cachedAppsList != null) {
            cachedAppsList.clear();
        }
    }

    public static void populateCachedAppsList (Context context) {
        cachedAppsList.clear();

        final PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        String al0PackageName = context.getPackageName();

        for (ApplicationInfo packageInfo : packages) {
            // check package name is not null and is not AL0
            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null && !packageInfo.packageName.equals(al0PackageName)) {
                ListItem listItem = new ListItem(packageInfo.packageName, packageInfo.loadLabel(packageManager).toString(), false);
                cachedAppsList.add(listItem);
            }
        }

        // sort packages by name
        Collections.sort(cachedAppsList, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.getText().toLowerCase().compareTo(o2.getText().toLowerCase());
            }
        });
    }

    public static ArrayList<ListItem> getAppsList (Context context) {
        if (cachedAppsList.size() == 0) {
            populateCachedAppsList(context);
        }

        return cachedAppsList;
    }
}
