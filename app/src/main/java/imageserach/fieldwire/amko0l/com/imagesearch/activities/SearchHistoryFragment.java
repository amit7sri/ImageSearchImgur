package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.adapter.MyItemRecyclerViewAdapter;

import static android.content.Context.MODE_PRIVATE;


public class SearchHistoryFragment extends Fragment {
    private static final String TAG = SearchHistoryFragment.class.getName();

    private static final String ARG_COLUMN_COUNT = "column-count";

    private String searchHistory = "SEARCH_HISTORY_FIELDWIRE";
    private String prefName = "SEARCH_HISTORY";
    private TextView emptyTextView;
    private MyItemRecyclerViewAdapter.OnListFragmentInteractionListener mListener;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    LinkedList<String> searchHistoryArray;
    private MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;


    public SearchHistoryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyItemRecyclerViewAdapter.OnListFragmentInteractionListener) {
            mListener = (MyItemRecyclerViewAdapter.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException("Class Cast Excecption");
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchHistoryFragment newInstance() {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getSharedPreferences(prefName, MODE_PRIVATE);
        searchHistoryArray = new LinkedList<>();
    }

    private void updateHistoryList() {
        searchHistoryArray.clear();
        Set<String> historyset = sharedPref.getStringSet(searchHistory, new LinkedHashSet<String>());
        searchHistoryArray.addAll(historyset);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        emptyTextView = view.findViewById(R.id.empty);
        if (!searchHistoryArray.isEmpty()) {
            emptyTextView.setVisibility(View.GONE);
        }
        // Set the adapter
        if (view instanceof LinearLayout) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(searchHistoryArray, mListener);
            recyclerView.setAdapter(myItemRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHistoryList();
        myItemRecyclerViewAdapter.notifyDataSetChanged();
    }

    // Handle query from seacrchWidget
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchString = intent.getStringExtra(SearchManager.QUERY);
            Set<String> set = sharedPref.getStringSet(searchHistory, new LinkedHashSet<String>());
            Set<String> newSet = new LinkedHashSet<>();
            newSet.addAll(set);
            newSet.add(searchString);
            Log.d(TAG  , "set is " + newSet);
            editor = getActivity().getSharedPreferences(prefName, MODE_PRIVATE).edit();
            editor.putStringSet(searchHistory, newSet);
            editor.apply();
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }
}
