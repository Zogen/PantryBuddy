package com.example.pantrybuddy;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final Context context;
    private List<PantryItem> pantryList;
    private DatabaseHelper dbHelper;
    private OnPantryItemChangeListener pantryItemChangeListener; // Interface for change listener

    public PantryAdapter(Context context, List<PantryItem> pantryList, DatabaseHelper dbHelper, OnPantryItemChangeListener pantryItemChangeListener) {
        this.context = context;
        this.pantryList = pantryList;
        this.dbHelper = dbHelper;
        this.pantryItemChangeListener = pantryItemChangeListener; // Initialize listener
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
        PantryActivity activity = (PantryActivity) context;

        // Set the pantry item name
        holder.pantryItemName.setText(item.getName());

        // Handle expiration date display and color change for (to-be) expired items
        if (item.getExpirationDate() != null && !item.getExpirationDate().isEmpty()) {
            holder.itemExpirationDateTextView.setText(item.getExpirationDate());
            holder.itemExpirationDateTextView.setVisibility(View.VISIBLE);

            // Define date format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            try {
                // Parse the expiration date
                Date expirationDate = sdf.parse(item.getExpirationDate());
                Date currentDate = new Date(); // Current date

                // Calculate the difference in days between today and expiration date
                long diffInMillies = expirationDate.getTime() - currentDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                // Set color based on the difference in days
                if (diffInDays >= 0 && diffInDays < 3) {
                    holder.itemExpirationDateTextView.setText(item.getExpirationDate());
                    holder.itemExpirationDateTextView.setTextColor(Color.RED); // About to expire (within 3 days)
                } else if (diffInDays < 0) {
                    holder.itemExpirationDateTextView.setText("EXPIRED");
                    holder.itemExpirationDateTextView.setTextColor(Color.RED); // Expired
                } else {
                    holder.itemExpirationDateTextView.setText(item.getExpirationDate());
                    holder.itemExpirationDateTextView.setTextColor(Color.parseColor("#AAAAAA")); // Default text color
                }
            } catch (ParseException e) {
                // Default text color
                holder.itemExpirationDateTextView.setTextColor(Color.parseColor("#AAAAAA"));
            }
        } else {
            // Set view as invisible as long as expiration date is not set
            holder.itemExpirationDateTextView.setVisibility(View.INVISIBLE);
        }

        // Set the quantity
        holder.pantryItemQuantity.setText(String.valueOf(item.getQuantity()));

        // Set click listener for the "X" button to remove item
        holder.removePantryItemButton.setOnClickListener(v -> {
            // Remove item from database
            dbHelper.deletePantryItem(item.getId());
            // Remove item from list and notify adapter
            pantryList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pantryList.size());

            // Notify the listener (PantryActivity) that an item has been removed, will trigger API call for recipe activity
            if (pantryItemChangeListener != null) {
                pantryItemChangeListener.onPantryItemRemoved();
            }
        });

        // Long press listener to update expiration date
        holder.itemView.setOnLongClickListener(v -> {
            showSetExpirationDateDialog(item);
            return true;
        });
    }

    // Dialog for expiration date picker input
    private void showSetExpirationDateDialog(PantryItem pantryItem) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year1, month1, dayOfMonth) -> {
                    String expirationDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    pantryItem.setExpirationDate(expirationDate);
                    dbHelper.updatePantryItem(pantryItem);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Expiration date set", Toast.LENGTH_SHORT).show();
                }, year, month, day);

        datePickerDialog.setTitle("Update Expiration Date");
        datePickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return pantryList.size();
    }


    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView pantryItemName, pantryItemQuantity, itemExpirationDateTextView;
        Button removePantryItemButton;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            pantryItemName = itemView.findViewById(R.id.pantryItemName);
            itemExpirationDateTextView = itemView.findViewById(R.id.itemExpirationDateTextView);
            pantryItemQuantity = itemView.findViewById(R.id.pantryItemQuantity);
            removePantryItemButton = itemView.findViewById(R.id.removePantryItemButton);
        }
    }

    // Interface to notify when a pantry item is removed
    public interface OnPantryItemChangeListener {
        void onPantryItemRemoved();
    }
}
