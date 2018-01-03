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

public class AdapterRecordMeeting extends RecyclerView.Adapter<AdapterRecordMeeting.ViewHolder> {
    private List<RecordMeeting> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;
        private List<RecordMeeting> mDataset;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            final TextView meetingDescription= (TextView) mLinearLayout
                    .findViewById(R.id.meeting_item_description);
            final TextView meetingDate = (TextView)mLinearLayout.findViewById(R.id
                    .meeting_item_date);
            final TextView meetingId = (TextView)mLinearLayout.findViewById(R.id.meeting_item_id);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //save the info
                    SharedPreferences sharedPreferences = mLinearLayout.getContext()
                            .getSharedPreferences("meetingInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("meetingDescription", meetingDescription.getText().toString());
                    editor.putString("meetingDate", meetingDate.getText().toString());
                    editor.putString("meetingId", meetingId.getText().toString());
                    Toast.makeText(mLinearLayout.getContext(),  meetingId.getText().toString(), Toast.LENGTH_SHORT).show();
                    editor.apply();
                    //start the activity
                    Intent intent = new Intent(mLinearLayout.getContext(), AddMeeting.class );
                    mLinearLayout.getContext().startActivity(intent);

                    //in the other activity retrieve the info.
                    Log.d("ITEMS LIST CLICKED", "Position: " + position);
                }
            });
        }
    }



    public AdapterRecordMeeting(List<RecordMeeting> projectMeetings) {
        mDataset = projectMeetings;
    }

    @Override
    public AdapterRecordMeeting.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_meeting, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView meetingDate = (TextView) holder.mLinearLayout.findViewById(R.id
                .meeting_item_date);
        TextView meetingDescription = (TextView) holder.mLinearLayout.findViewById(R.id
                .meeting_item_description);
        TextView meetinID = (TextView) holder.mLinearLayout.findViewById(R.id
                .meeting_item_id);

        meetinID.setText(mDataset.get(position).id);
        meetingDescription.setText(mDataset.get(position).description);

        Calendar theDate = Calendar.getInstance();
        theDate.setTimeInMillis(mDataset.get(position).meetingDate);
        String dateString = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                (theDate.getTime());
        meetingDate.setText(dateString);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}