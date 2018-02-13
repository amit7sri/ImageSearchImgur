package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.adapter.MyImageHoldingRecyclerViewAdapter;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.ConnectivityUtils;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.Utils;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.VolleySingleton;
import imageserach.fieldwire.amko0l.com.imagesearch.listeners.EndlessRecyclerViewScrollListener;

import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.ARG_COLUMN_COUNT;
import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.SEARCH_STRING;

public class ImageHolderFragment extends Fragment {

    private static final String TAG = "ImageHolderFragment";
    private int mColumnCount = 2;
    private OnImageHolderIInteractionListener mListener;
    private String searchString;
    BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private ProgressBar loadingIndicator;
    private RecyclerView recyclerView;
    private MyImageHoldingRecyclerViewAdapter myImageHoldingRecyclerViewAdapter;
    private TextView emptyTextView;
    private List<String> imageList;
    private int offset = 1;
    private EndlessRecyclerViewScrollListener scrollListener;

    public ImageHolderFragment() {
    }

    public static ImageHolderFragment newInstance(int columnCount, String searchQuery) {
        ImageHolderFragment fragment = new ImageHolderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SEARCH_STRING, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            searchString = getArguments().getString(SEARCH_STRING);
        }

        Utils.setContext(getActivity());

        imageList = new ArrayList<>();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "   network status on receive");
                updateUI();

            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);

        loadingIndicator = (ProgressBar) view.findViewById(R.id.loadIndicator);
        emptyTextView = (TextView) view.findViewById(R.id.empty);

        if (!ConnectivityUtils.isConnected(getActivity())) {
            emptyTextView.setText(R.string.no_internet);
        }

        // Set the adapter
        if (view instanceof LinearLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.recyclerView);
            myImageHoldingRecyclerViewAdapter = new MyImageHoldingRecyclerViewAdapter(imageList, mListener, getActivity());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mColumnCount);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(myImageHoldingRecyclerViewAdapter);
            scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    loadNextDataFromApi();
                }
            };
            // Adds the scroll listener to RecyclerView
            recyclerView.addOnScrollListener(scrollListener);
        }
        Log.d(TAG,"on create view called  " +offset);
        volleyRequest(getUri(), 0);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImageHolderIInteractionListener) {
            mListener = (OnImageHolderIInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Volley request for json string
    public void volleyRequest(String volleysearchString, final int addFlag) {
        Log.d(TAG, "volley request");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, volleysearchString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingIndicator.setVisibility(View.GONE);
                        updateUIPostExecute(Utils.extractImages(response), addFlag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingIndicator.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                Activity activity = getActivity();
                String message = "Error";
                if(activity!=null) {
                    if (error instanceof NetworkError) {
                        message = getResources().getString(R.string.connection_error);
                    } else if (error instanceof ServerError) {
                        message = getResources().getString(R.string.server_error);
                    } else if (error instanceof AuthFailureError) {
                        message = getResources().getString(R.string.connection_error);
                    } else if (error instanceof ParseError) {
                        message = getResources().getString(R.string.parse_error);
                    } else if (error instanceof TimeoutError) {
                        message = getResources().getString(R.string.timeout_error);
                    }
                }
                emptyTextView.setText(message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Client-ID 41a1c63ff97b89d");
                return headers;
            }
        };
        VolleySingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(this);
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    public void updateUIPostExecute(List<String> response, int addFlag) {

        if (addFlag == 0) {
            imageList.clear();
        }

        if(response != null  || response.size()!=0){
            imageList.addAll(response);
            myImageHoldingRecyclerViewAdapter.notifyDataSetChanged();
        }else{
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_results);
        }

    }

    public String getSearchString() {
        return searchString;
    }

    public String getUri() {
        return Utils.getUri(getSearchString()).toString();
    }

    public void loadNextDataFromApi() {
        loadingIndicator.setVisibility(View.VISIBLE);
        offset++;
        // 1. First, clear the array of data
        //imageList.clear();
        // 2. Notify the adapter of the update
        //myImageHoldingRecyclerViewAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
        // 3. Reset endless scroll listener when performing a new search
        //scrollListener.resetState();
        Log.d(TAG, "loadNextDataFromApi  " +offset);
        Uri.Builder uriBuilder = Utils.getUri(getSearchString(), offset);
        uriBuilder.appendQueryParameter("start", "" + offset);
        volleyRequest(uriBuilder.toString(), 1);
    }

    private void updateUI() {
        if (ConnectivityUtils.isConnected(getActivity())) {
            emptyTextView.setText(R.string.no_images);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public interface OnImageHolderIInteractionListener {
        void onListImageHolderInteraction(String imageUrl, ImageView imageView, ProgressBar progressBar);
    }
}
