package ru.mirea.censusinfo.util;

import android.content.Context;

/** Пока что «всегда в сети» – только чтобы проект собирался. */
public class ConnectivityUtil {
    public static boolean hasNetwork()              { return true; }
    public static boolean hasNetwork(Context ctx)   { return true; }
}
