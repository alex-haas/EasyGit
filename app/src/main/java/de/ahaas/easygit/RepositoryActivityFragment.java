package de.ahaas.easygit;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.ViewById;
import org.eclipse.jgit.api.Git;

import java.util.List;

import de.ahaas.easygit.helper.GitHelper;

@EFragment(R.layout.repository_fragment)
public class RepositoryActivityFragment extends Fragment {
    private final static String TAG = RepositoryActivityFragment.class.getName();

    @ViewById(R.id.repositoryList)
    ListView repositoryList;

    ArrayAdapter<Git> repositoryAdapter;

    @AfterViews
    void initGui(){
        List<Git> repoList = GitHelper.getRepoList(getContext());
        repositoryAdapter =  new ArrayAdapter<Git>(getContext(), android.R.layout.simple_list_item_1, repoList);
        repositoryList.setAdapter(repositoryAdapter);
    }


    @ItemClick(R.id.repositoryList)
    public void myListItemClicked(int position) {
        Log.i(TAG, "Clicked on position: "+position);
    }

    @ItemLongClick(R.id.repositoryList)
    public void myListItemLongClicked(int position) {
        Log.i(TAG, "Long Clicked on position: "+position);
    }
}
