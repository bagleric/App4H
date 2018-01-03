package com.example.bagle.app4h;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by bagle on 12/1/2017.
 */

public class AdapterRecordGoal extends RecyclerView.Adapter<AdapterRecordGoal.ViewHolder> {
    private List<RecordGoal> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            final TextView goalTitle = (TextView) mLinearLayout.findViewById(R.id
                    .goal_item_description_title);
            final TextView goalDescription = (TextView) mLinearLayout.findViewById(R.id
                    .goal_item_description);
            final TextView toDoList = (TextView) mLinearLayout.findViewById(R.id
                    .goal_item_to_do);
            final TextView deadline = (TextView) mLinearLayout.findViewById(R.id.goal_deadline_date);
            final TextView id = (TextView) mLinearLayout.findViewById(R.id.goal_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //save the info
                    SharedPreferences sharedPreferences = mLinearLayout.getContext()
                            .getSharedPreferences("goalInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("goalDescription", goalDescription.getText().toString());
                    editor.putString("goalTodo", toDoList.getText().toString());
                    editor.putString("goalDeadline", deadline.getText().toString());
                    editor.putString("goalId", id.getText().toString());
                    Toast.makeText(mLinearLayout.getContext(), "Editing Record", Toast
                            .LENGTH_SHORT).show();
                    editor.apply();
                    //start the activity
                    Intent intent = new Intent(mLinearLayout.getContext(), AddGoal.class );
                    mLinearLayout.getContext().startActivity(intent);

                    Log.d("ITEMS LIST CLICKED", "Position: " + position);

                }
            });
        }
    }

    public AdapterRecordGoal(List<RecordGoal> projectGoals) {
        mDataset = projectGoals;
    }

    @Override
    public AdapterRecordGoal.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_goal, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView goalTitle = (TextView) holder.mLinearLayout.findViewById(R.id
                .goal_item_description_title);
        TextView goalDescription = (TextView) holder.mLinearLayout.findViewById(R.id
                .goal_item_description);
        TextView toDoList = (TextView) holder.mLinearLayout.findViewById(R.id
                .goal_item_to_do);
        TextView deadline = (TextView) holder.mLinearLayout.findViewById(R.id.goal_deadline_date);
        TextView id = (TextView) holder.mLinearLayout.findViewById(R.id.goal_id);

        String titleText = "Goal #" + String.valueOf(position + 1);
        goalTitle.setText(titleText);
        goalDescription.setText(mDataset.get(position).description);
        toDoList.setText(mDataset.get(position).toDos);
        id.setText(mDataset.get(position).id);

        Calendar theDeadline = Calendar.getInstance();
        theDeadline.setTimeInMillis(mDataset.get(position).deadline);
        String dueDate = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format(theDeadline
                .getTime());
        deadline.setText(dueDate);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}