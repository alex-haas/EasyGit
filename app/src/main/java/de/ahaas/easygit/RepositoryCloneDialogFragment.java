package de.ahaas.easygit;

import android.Manifest;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SupposeUiThread;
import org.androidannotations.annotations.ViewById;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.io.IOException;

import de.ahaas.easygit.helper.PermissionHelper;
import de.ahaas.easygit.helper.SharedPrefHelper;

@EFragment(R.layout.repository_clone_dialog)
public class RepositoryCloneDialogFragment extends DialogFragment {
    private final static String TAG = RepositoryCloneDialogFragment.class.getName();

    private final int PERMISSION_REQUEST_CODE = 0x44;
    private final String[] NECESSARY_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    @ViewById(R.id.cloneUrl)
    EditText cloneUrlView;
    @ViewById(R.id.repoName)
    EditText repoNameView;
    @ViewById(R.id.cloneSubmodules)
    CheckBox cloneSubmodulesView;
    @ViewById(R.id.checkout)
    CheckBox checkoutView;
    @ViewById(R.id.username)
    EditText usernameView;
    @ViewById(R.id.userpassword)
    EditText userpasswordView;
    @ViewById(R.id.cancel)
    Button cancelButton;
    @ViewById(R.id.submit)
    Button submitButton;
    @ViewById(R.id.status)
    TextView statusView;

    private ProgressMonitor cloneProgressMonitor = new ProgressMonitor() {
        private String workTitle;
        private int totalWork;
        private int completedWork;
        private int totalTasks;
        private int completedTasks;

        @Override
        public void start(int totalTasks) {
            this.totalTasks = totalTasks;
            this.completedTasks = 0;
        }

        @Override
        public void beginTask(String title, int totalWork) {
            this.workTitle = title;
            this.completedWork = 0;
            this.totalWork = totalWork;
            updateStatus();
        }

        @Override
        public void update(int completed) {
            this.completedWork++;
            updateStatus();
        }

        @Override
        public void endTask() {
            this.completedTasks++;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        private void updateStatus() {
            statusView.post(() -> {
                if (completedTasks > totalTasks){
                    statusView.setText("Done");
                } else {
                    statusView.setText("(" + completedTasks + "/" + totalTasks + ") " + workTitle + ": " + completedWork + "/" + totalWork);
                }
            });
        }
    };

    private Runnable cloneTask = () -> {
        String cloneUrl            = cloneUrlView.getText().toString();
        String repoName            = repoNameView.getText().toString();
        boolean cloneSubmodules    = cloneSubmodulesView.isChecked();
        boolean checkout           = checkoutView.isChecked();
        String username            = usernameView.getText().toString();
        String userpassword        = userpasswordView.getText().toString();

        SharedPrefHelper.setLastUsername(getContext(), username);
        SharedPrefHelper.setLastUserpassword(getContext(), userpassword);

        File repoFile = new File(SharedPrefHelper.getDefaultRepositoryPath(getContext()), repoName);
        if(repoFile.exists()){
            new AlertDialog.Builder(getContext())
                    .setTitle("Repository already exists")
                    .setMessage("The repository you are trying to clone already exists in the working directory. Delete it and clone afterwards?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            FileUtils.delete(repoFile, FileUtils.RECURSIVE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.cloneTask.run();
                    })
                    .create()
                    .show();
            setDialogElementsEnabled(true);
            return;
        }

        RepositoryCloneTask cloneTask = new RepositoryCloneTask();
        cloneTask.setCloneUrl(cloneUrl);
        cloneTask.setProgressMonitor(cloneProgressMonitor);
        cloneTask.setCheckout(checkout);
        cloneTask.setCloneSubmodules(cloneSubmodules);
        cloneTask.setDirectoryName(repoFile);
        cloneTask.setOnComplete(() -> {
            dismiss();
        });
        cloneTask.execute();
    };

    private TextWatcher moduleNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String cloneUrl = s.toString();
            if(cloneUrl.length() > 5 && cloneUrl.indexOf('/') != -1){
                String repoName = cloneUrl.substring(cloneUrl.lastIndexOf('/') + 1, cloneUrl.length());
                if(repoName.endsWith(".git")){
                    repoName = repoName.substring(0,repoName.length()-4);
                }
                repoNameView.setText(repoName);
            }
        }
    };

    @AfterViews
    void initGui(){
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);
        cloneUrlView.addTextChangedListener(moduleNameWatcher);

        usernameView.setText(SharedPrefHelper.getLastUsername(getContext()));
        userpasswordView.setText(SharedPrefHelper.getLastUserpassword(getContext()));
    }

    @Click(R.id.submit)
    void cloneRepo(){
        setDialogElementsEnabled(false);
        PermissionHelper.executeOrAskForPermission(this, getActivity(), NECESSARY_PERMISSIONS, PERMISSION_REQUEST_CODE, cloneTask);
    }

    @SupposeUiThread
    void setDialogElementsEnabled(boolean enabled){
        submitButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult with requestCode "+requestCode);
        for(int i=0; i<permissions.length; i++) {
            Log.i(TAG, permissions[i]+": "+grantResults[i]);
        }
        PermissionHelper.executeOrAskForPermission(this, getActivity(), NECESSARY_PERMISSIONS, PERMISSION_REQUEST_CODE, cloneTask);
    }

    @Click(R.id.cancel)
    void cancelClicked(){
        dismiss();
    }

}
