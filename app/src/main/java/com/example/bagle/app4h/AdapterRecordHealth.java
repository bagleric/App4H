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
 * Created by bagle on 12/8/2017.
 */

public class AdapterRecordHealth extends RecyclerView.Adapter<AdapterRecordHealth.ViewHolder> {
    private List<RecordHealth> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            final TextView species = (TextView) mLinearLayout.findViewById(R.id
                    .health_item_species);
            final TextView condition = (TextView) mLinearLayout.findViewById(R.id
                    .health_item_condition);
            final TextView productUsed = (TextView) mLinearLayout.findViewById(R.id
                    .health_item_product_used);
            final TextView comments = (TextView) mLinearLayout.findViewById(R.id
                    .health_item_comments);
            final TextView date = (TextView) mLinearLayout.findViewById(R.id.health_item_date);
            final TextView id = (TextView) mLinearLayout.findViewById(R.id.health_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //save the info
                    SharedPreferences sharedPreferences = mLinearLayout.getContext()
                            .getSharedPreferences("healthInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("healthSpecies", species.getText().toString());
                    editor.putString("healthCondition", condition.getText().toString());
                    editor.putString("healthProductUsed", productUsed.getText().toString());
                    editor.putString("healthComments", comments.getText().toString());
                    editor.putString("healthDate", date.getText().toString());
                    editor.putString("healthId", id.getText().toString());
                    Toast.makeText(mLinearLayout.getContext(), "Editing Record", Toast
                            .LENGTH_SHORT).show();
                    editor.apply();
                    //start the activity
                    Intent intent = new Intent(mLinearLayout.getContext(), AddHealth.class );
                    mLinearLayout.getContext().startActivity(intent);

                    Log.d("ITEMS LIST CLICKED", "Position: " + position);

                }
            });
        }
    }

    public AdapterRecordHealth(List<RecordHealth> projectHealthItems) {
        mDataset = projectHealthItems;
    }

    @Override
    public AdapterRecordHealth.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_health, parent, false);
        AdapterRecordHealth.ViewHolder vh = new AdapterRecordHealth.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AdapterRecordHealth.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView species = (TextView) holder.mLinearLayout.findViewById(R.id
          .health_item_species);
        TextView condition = (TextView) holder.mLinearLayout.findViewById(R.id
          .health_item_condition);
        TextView productUsed = (TextView) holder.mLinearLayout.findViewById(R.id
          .health_item_product_used);
        TextView comments = (TextView) holder.mLinearLayout.findViewById(R.id
          .health_item_comments);
        TextView date = (TextView) holder.mLinearLayout.findViewById(R.id.health_item_date);
        TextView id = (TextView) holder.mLinearLayout.findViewById(R.id.health_id);

        TextView healthTitle = (TextView) holder.mLinearLayout.findViewById(R.id.health_item_title);
        String titleText = "Health Record #" + String.valueOf(position + 1);
        healthTitle.setText(titleText);

        id.setText(mDataset.get(position).id);

        Calendar theDeadline = Calendar.getInstance();
        theDeadline.setTimeInMillis(mDataset.get(position).date);
        String dueDate = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format(theDeadline
                .getTime());
        date.setText(dueDate);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
