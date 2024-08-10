package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private List<PantryItem> pantryList;
    private DatabaseHelper dbHelper;

    public PantryAdapter(Context context, List<PantryItem> pantryList, DatabaseHelper dbHelper) {
        this.context = context;
        this.pantryList = pantryList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = pantryList.get(position);
        holder.pantryItemName.setText(item.getName());
        holder.pantryItemQuantity.setText(String.valueOf(item.getQuantity()));

        // Set click listener for the "X" button to remove item
        holder.removePantryItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove item from database
                dbHelper.deletePantryItem(item.getId());
                // Remove item from list and notify adapter
                pantryList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, pantryList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantryList.size();
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView pantryItemName, pantryItemQuantity;
        Button removePantryItemButton;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            pantryItemName = itemView.findViewById(R.id.pantryItemName);
            pantryItemQuantity = itemView.findViewById(R.id.pantryItemQuantity);
            removePantryItemButton = itemView.findViewById(R.id.removePantryItemButton);
        }
    }
}
