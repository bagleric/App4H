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

import java.util.List;

/**
 * Created by bagle on 11/23/2017.
 */

public class AdapterRecordFinance extends RecyclerView.Adapter<AdapterRecordFinance.ViewHolder> {
    private List<RecordFinance> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            final TextView description = (TextView) mLinearLayout.findViewById(R.id
                    .finance_record_description);
            final TextView amount = (TextView) mLinearLayout.findViewById(R.id.finance_record_amount);

            final TextView id = mLinearLayout.findViewById(R.id.finance_id);
            final TextView isExpense = mLinearLayout.findViewById(R.id.finance_is_expense);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //save the info
                    String text = amount.getText().toString();
                    String amount = text.replaceAll("[^\\d.]+", "");
                    SharedPreferences sharedPreferences = mLinearLayout.getContext()
                            .getSharedPreferences("financeInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("financeDescription", description.getText().toString());
                    editor.putString("financeAmount",amount );
                    editor.putString("financeId", id.getText().toString());
                    editor.putString("financeIsExpense", isExpense.getText().toString());
                    editor.apply();
                    Toast.makeText(mLinearLayout.getContext(), "Editing Record", Toast
                            .LENGTH_SHORT).show();
                    Intent intent = new Intent(mLinearLayout.getContext(), AddFinance.class);
                    mLinearLayout.getContext().startActivity(intent);

                    Log.d("ITEMS LIST CLICKED", "Position: " + position);
                }
            });
        }
    }

    public AdapterRecordFinance(List<RecordFinance> financeRecords) {
        mDataset = financeRecords;
    }

    @Override
    public AdapterRecordFinance.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_finance, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView description = (TextView) holder.mLinearLayout.findViewById(R.id
                .finance_record_description);
        TextView amount = (TextView) holder.mLinearLayout.findViewById(R.id.finance_record_amount);
        TextView id = (TextView) holder.mLinearLayout.findViewById(R.id.finance_id);
        TextView isExpense = (TextView) holder.mLinearLayout.findViewById(R.id.finance_is_expense);


        description.setText(mDataset.get(position).description);
        String value = "";
        if (mDataset.get(position).isExpense) {
            value = "$ (" + mDataset.get(position).amount + ")";
            amount.setText(value);
            value = "true";
        } else {
            value = "$ " + mDataset.get(position).amount;
            amount.setText(value);
            value = "false";
        }
        id.setText(mDataset.get(position).id);
        isExpense.setText(value);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
