package imageserach.fieldwire.amko0l.com.imagesearch.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import imageserach.fieldwire.amko0l.com.imagesearch.R;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
    private final String TAG = MyItemRecyclerViewAdapter.class.getName();
    private final LinkedList<String> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(LinkedList<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(TAG, "Item click listener1");
        holder.mContentView.setText(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final LinearLayout mView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = (LinearLayout) view.findViewById(R.id.search_fragment_item);
            mContentView = (TextView) view.findViewById(R.id.content);

            view.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onListFragmentInteraction(mContentView.getText().toString());
            }
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String item);
    }
}
