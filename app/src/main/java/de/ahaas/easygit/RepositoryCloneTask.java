package de.ahaas.easygit;

import android.os.AsyncTask;
import android.widget.Toast;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * Created by Alex on 11.04.2016.
 */

public class RepositoryCloneTask extends AsyncTask<String, String, Git> {
    private String cloneUrl;
    private File directory;
    private File gitDirectory;

    private boolean bare = false;
    private boolean cloneAllBranches = true;
    private boolean cloneSubmodules;
    private boolean checkout;
    private ProgressMonitor progressMonitor;
    private Runnable onComplete;

    private String username;
    private String password;

    protected Git doInBackground(String... cloneUrl) {
        Git result = null;
        try {
            result = Git.cloneRepository()
                    .setBare(this.bare)
                    .setCloneAllBranches(this.cloneAllBranches)
                    .setCloneSubmodules(this.cloneSubmodules)
                    .setDirectory(this.bare ? this.gitDirectory : this.directory)
                    .setGitDir(this.gitDirectory)
                    .setNoCheckout(!this.checkout)
                    .setProgressMonitor(progressMonitor)
                    .setURI(this.cloneUrl)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("gravitor","Pilipenk0"))
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPostExecute(Git git) {
        if(onComplete != null){
            onComplete.run();
        }
    }

    public void setDirectoryName(File repoPath) {
        this.directory = repoPath;
        this.setGitDirectory(new File(this.directory, ".git"));
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public void setGitDirectory(File gitDirectory) {
        this.gitDirectory = gitDirectory;
    }

    public void setCloneSubmodules(boolean cloneSubmodules) {
        this.cloneSubmodules = cloneSubmodules;
    }

    public void setCheckout(boolean checkout) {
        this.checkout = checkout;
    }

    public void setProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}
