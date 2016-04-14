package de.ahaas.easygit.helper;

import android.content.Context;
import android.content.SharedPreferences;

import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 09.04.2016.
 */
public class GitHelper {
    public static List<Git> getRepoList(Context context){
        Set<String> repoPaths = SharedPrefHelper.getRepositoryPaths(context);
        List<Git> repoList = new ArrayList<>(repoPaths.size());
        Git git;
        for(String repoPath : repoPaths){
            try {
                git = Git.open(new File(repoPath));
                repoList.add(git);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return repoList;
    }
}
