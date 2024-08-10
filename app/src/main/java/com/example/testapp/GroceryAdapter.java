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

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {

    private Context context;
    private List<GroceryItem> groceryList;
    private DatabaseHelper dbHelper;

    public GroceryAdapter(Context context, List<GroceryItem> groceryList, DatabaseHelper dbHelper) {
        this.context = context;
        this.groceryList = groceryList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        GroceryItem item = groceryList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        // Set click listener for the "X" button to remove item
        holder.removeItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove item from database
                dbHelper.deleteGroceryItem(item.getId());
                // Remove item from list and notify adapter
                groceryList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, groceryList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public static class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity;
        Button removeItemButton;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            removeItemButton = itemView.findViewById(R.id.removeItemButton);
        }
    }
}
