package de.ahaas.easygit.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class SharedPrefHelper {
    private final static String SP_KEY = "easy git shared prefs";
    private final static String SP_DEFAULT_CLONE_PATH = "easy git default clone path";
    private final static String SP_DEFAULT_CLONE_PATH_DEFAULT = new File(Environment.getExternalStorageDirectory().getPath(), "EasyGit").getPath();
    private final static String SP_REPO_PATHS = "easy git repo paths";
    private final static Set<String> SP_REPO_PATHS_DEFAULT = new HashSet<>(0);

    private final static String SP_USERNAME = "easy git user name";
    private final static String SP_USERPASSWORD = "easy git user password";

    private static SharedPreferences instance;
    public static SharedPreferences get(Context context){
        if(instance == null){
            instance = context.getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);
        }
        return instance;
    }

    public static String getDefaultRepositoryPath(Context context){
        return get(context).getString(SP_DEFAULT_CLONE_PATH, SP_DEFAULT_CLONE_PATH_DEFAULT);
    }

    public static void setDefaultRepositoryPath(Context context, String newDefaultRepositoryPath){
        get(context).edit().putString(SP_DEFAULT_CLONE_PATH, newDefaultRepositoryPath).commit();
    }

    public static Set<String> getRepositoryPaths(Context context){
        return get(context).getStringSet(SP_REPO_PATHS, SP_REPO_PATHS_DEFAULT);
    }

    public static void setRepositoryPaths(Context context, Set<String> newRepositoryPaths){
        get(context).edit().putStringSet(SP_REPO_PATHS, newRepositoryPaths).commit();
    }

    public static String getLastUsername(Context context){
        return get(context).getString(SP_USERNAME, "");
    }

    public static void setLastUsername(Context context, String username){
        get(context).edit().putString(SP_USERNAME, username).commit();
    }

    public static String getLastUserpassword(Context context){
        return get(context).getString(SP_USERPASSWORD, "");
    }

    public static void setLastUserpassword(Context context, String userpassword){
        get(context).edit().putString(SP_USERPASSWORD, userpassword).commit();
    }
}
