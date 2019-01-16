package com.bumthing.shi.feedback;


import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.widget.*;
import android.content.Context;
import android.view.ViewGroup;
import android.view.*;
import data.Feedback;
import java.util.ArrayList;
import java.util.List;


public class RAdapter extends RecyclerView.Adapter<RAdapter.RViewHolder> {
    private ArrayList<Feedback> mFeedbackList;
    final private ItemClickListener mClickListener;
    private int count=0;

    public interface ItemClickListener {
        void onItemClickListener(long itemId);
    }
    //ViewHolder for them feedbacks
    public class RViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView descTextView;
        public TextView userTextView;



        public RViewHolder(View v) {
            super(v);
            descTextView = v.findViewById(R.id.tv_short_desc);
            userTextView = v.findViewById(R.id.tv_short_user_details);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            long elementId = mFeedbackList.get(getAdapterPosition()).getFId();
            mClickListener.onItemClickListener(elementId);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RAdapter(ArrayList myDataset, ItemClickListener listener) {
        mFeedbackList = myDataset;
        mClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RAdapter.RViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);

        RViewHolder vh = new RViewHolder(ll);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        holder.userTextView.setText(mFeedbackList.get(position).getName());
        holder.descTextView.setText(mFeedbackList.get(position).getMssg());

    }

    public void setList(List<Feedback> l){
        mFeedbackList = (ArrayList<Feedback>) l;
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFeedbackList.size();
    }
}
