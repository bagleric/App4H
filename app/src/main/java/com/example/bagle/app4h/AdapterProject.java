package com.example.bagle.app4h;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AdapterProject extends RecyclerView.Adapter<AdapterProject.ViewHolder> {
    private List<RecordProject> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;
        private List<RecordProject> mDataset;
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
//                    SharedPreferences sharedPreferences = mLinearLayout.getContext()
//                            .getSharedPreferences("meetingInfo", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("meetingDescription", meetingDescription.getText().toString());
//                    editor.putString("meetingDate", meetingDate.getText().toString());
//                    editor.putString("meetingId", meetingId.getText().toString());
//                    Toast.makeText(mLinearLayout.getContext(),  meetingId.getText().toString(), Toast.LENGTH_SHORT).show();
//                    editor.apply();
//                    //start the activity
//                    Intent intent = new Intent(mLinearLayout.getContext(), AddMeeting.class );
//                    mLinearLayout.getContext().startActivity(intent);

                    //in the other activity retrieve the info.
                    Log.d("ITEMS LIST CLICKED", "Position: " + position);
                }
            });
        }
    }



    public AdapterProject(List<RecordProject> projects) {
        mDataset = projects;
    }

    @Override
    public AdapterProject.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_project, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        TextView projectDate = (TextView) holder.mLinearLayout.findViewById(R.id
//                .project_item_date);
//        TextView projectDescription = (TextView) holder.mLinearLayout.findViewById(R.id
//                .project_item_description);
//        TextView meetinID = (TextView) holder.mLinearLayout.findViewById(R.id
//                .project_item_id);
//
//        meetinID.setText(mDataset.get(position).id);
//        projectDescription.setText(mDataset.get(position).description);
//
//        Calendar theDate = Calendar.getInstance();
//        theDate.setTimeInMillis(mDataset.get(position).projectDate);
//        String dateString = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
//                (theDate.getTime());
//        projectDate.setText(dateString);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
//public class AdapterProjectList extends RecyclerView.Adapter<AdapterProjectList
//        .ProjectListAdapterViewHolder> {
//
//    private String[] mProjectNames;
//
//    public AdapterProjectList() {
//
//    }
//
//    /**
//     * Cache of the children views for a forecast list item.
//     */
//    public class ProjectListAdapterViewHolder extends RecyclerView.ViewHolder {
//
//        public final TextView mProjectBoxTextView;
//
//        public ProjectListAdapterViewHolder(View view) {
//            super(view);
//            mProjectBoxTextView = (TextView) view.findViewById(R.id.tv_project_name);
//        }
//    }
//
//    @Override
//    public ProjectListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        Context context = viewGroup.getContext();
//        int layoutIdForListItem = R.layout.record_project;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        boolean shouldAttachToParentImmediately = false;
//
//        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
//        return new ProjectListAdapterViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ProjectListAdapterViewHolder projectListAdapterViewHolder, int
//            position) {
//        String projectName = mProjectNames[position];
//        projectListAdapterViewHolder.mProjectBoxTextView.setText(projectName);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (null == mProjectNames) return 0;
//        return mProjectNames.length;
//    }
//
//    public void setWeatherData(String[] projectNames) {
//        mProjectNames = projectNames;
//        notifyDataSetChanged();
//    }
//}
//
////
////public class AdapterProjectList extends RecyclerView.Adapter<AdapterProjectList.ViewHolder> {
////    private ArrayList<String> mDataset;
////
////    public static class ViewHolder extends RecyclerView.ViewHolder{
////        // each data item is just a string in this case
////        public TextView mTextView;
////        public ViewHolder(TextView v) {
////            super(v);
////            mTextView = v;
////        }
////    }
////
////    // Provide a suitable constructor (depends on the kind of dataset)
////    public AdapterProjectList(ArrayList<String> myDataset) {
////        mDataset = myDataset;
////    }
////
////    // Create new views (invoked by the layout manager)
////    @Override
////    public AdapterProjectList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        // create a new view
////        TextView v = (TextView) LayoutInflater.from(parent.getContext())
////                .inflate(R.layout.record_project, parent, false);
////        // set the view's size, margins, paddings and layout parameters ...
////        ViewHolder vh = new ViewHolder(v);
////        return vh;
////    }
////
////    // Replace the contents of a view (invoked by the layout manager)
////    @Override
////    public void onBindViewHolder(ViewHolder holder, int position) {
////        // - get element from your dataset at this position
////        // - replace the contents of the view with that element
////        holder.mTextView.setText(mDataset.get(position));
////
////    }
////
////    // Return the size of your dataset (invoked by the layout manager)
////    @Override
////    public int getItemCount() {
////        return mDataset.size();
////    }
////}
