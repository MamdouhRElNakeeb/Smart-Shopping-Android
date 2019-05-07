package com.smartshopping;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductsAdapter  extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {


    Context context;
    ArrayList<Product> productArrayList;

    public ProductsAdapter (Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.name.setText(productArrayList.get(i).name);
        viewHolder.count.setText(String.valueOf(productArrayList.get(i).count));
        viewHolder.price.setText(String.valueOf(productArrayList.get(i).price) + " L.E");

        viewHolder.img.setImageResource(productArrayList.get(i).img);

        if (productArrayList.get(i).img != 0)
            viewHolder.img.setImageResource(productArrayList.get(i).img);
        else
            Picasso.get().load(productArrayList.get(i).image).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.img);


        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productArrayList.get(viewHolder.getAdapterPosition()).count++;
                viewHolder.count.setText(String.valueOf(productArrayList.get(viewHolder.getAdapterPosition()).count));
            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productArrayList.get(viewHolder.getAdapterPosition()).count == 1)
                    return;
                productArrayList.get(viewHolder.getAdapterPosition()).count--;
                viewHolder.count.setText(String.valueOf(productArrayList.get(viewHolder.getAdapterPosition()).count));
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productArrayList.remove(viewHolder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView name, count, price;
        ImageButton plus, minus, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.itemName);
            count = itemView.findViewById(R.id.counterTV);
            price = itemView.findViewById(R.id.price);
            delete = itemView.findViewById(R.id.delete);
            plus = itemView.findViewById(R.id.up);
            minus = itemView.findViewById(R.id.down);
        }
    }
}
