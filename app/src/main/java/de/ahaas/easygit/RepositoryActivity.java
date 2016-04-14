package de.ahaas.easygit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.repository_activity)
public class RepositoryActivity extends AppCompatActivity {
    private final static String TAG = RepositoryActivity.class.getName();

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void initGui(){
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repository, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Click(R.id.fab)
    void showDialog() {
        DialogFragment newFragment = new RepositoryCloneDialogFragment_();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
}
