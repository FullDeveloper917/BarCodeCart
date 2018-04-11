package com.example.david.barcodecart.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.david.barcodecart.CartActivity;
import com.example.david.barcodecart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 9/3/2017.
 */

public class HistoryProductAdapter extends ArrayAdapter{

    private final Context context;
    private List<Product> lists = new ArrayList<>();

    private TextView txtName;
    private TextView txtQty;
    private TextView txtPrice;
    private TextView txtDate;

    private CartActivity myActivity;

    public void setLists(List<Product> lists) {
        this.lists = lists;
    }

    public HistoryProductAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
    }

    public void setMyActivity(CartActivity myActivity) {
        this.myActivity = myActivity;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.product_detail, parent, false);

        txtName = rowView.findViewById(R.id.txtItemName);
        txtQty = rowView.findViewById(R.id.txtItemQty);
        txtPrice = rowView.findViewById(R.id.txtItemPrice);
        txtDate = rowView.findViewById(R.id.txtItemDate);

        txtName.setText(lists.get(position).getName());
        txtQty.setText(String.format("%.1f", lists.get(position).getQty()));
        txtPrice.setText(context.getResources().getString(R.string.inr) + String.format("%.2f", lists.get(position).getPrice()));
        txtDate.setText(lists.get(position).getTime());

        return rowView;
    }

}
