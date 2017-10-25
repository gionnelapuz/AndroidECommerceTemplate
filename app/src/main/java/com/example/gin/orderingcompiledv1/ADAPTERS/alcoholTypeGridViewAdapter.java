package com.example.gin.orderingcompiledv1.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.gin.orderingcompiledv1.VOLLEYREQUEST.ImageVolleyRequest;
import com.example.gin.orderingcompiledv1.R;

import java.util.ArrayList;

/**
 * Created by Gin on 5/14/2017.
 */

public class alcoholTypeGridViewAdapter extends BaseAdapter {

    private ImageLoader imageLoader;

    private Context context;

    private ArrayList<String> images;
    private ArrayList<String> names;
    private ArrayList<String> price;
    private ArrayList<String> prices;
    private ArrayList<String> size;
    private ArrayList<String> sizes;
    private ArrayList<String> quantity;

    public alcoholTypeGridViewAdapter(Context context, ArrayList<String> images, ArrayList<String> names, ArrayList<String> price, ArrayList<String> prices, ArrayList<String> size, ArrayList<String> sizes, ArrayList<String> quantity){
        this.context = context;
        this.images = images;
        this.names = names;
        this.price = price;
        this.prices = prices;
        this.size = size;
        this.sizes = sizes;
        this.quantity = quantity;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.alcohol_type_item, null);

        NetworkImageView thumbNail = (NetworkImageView) grid.findViewById(R.id.imageViewHero);
        imageLoader = ImageVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(images.get(position), ImageLoader.getImageListener(thumbNail, R.drawable.loading, android.R.drawable.ic_dialog_alert));

        TextView title = (TextView) grid.findViewById(R.id.textViewName);
        TextView textPrice = (TextView) grid.findViewById(R.id.textViewPrice);
        TextView textPrices = (TextView) grid.findViewById(R.id.textViewPrices);
        TextView textSize = (TextView) grid.findViewById(R.id.textViewSize);
        TextView textSizes = (TextView) grid.findViewById(R.id.textViewSizes);
        TextView textQuantity = (TextView) grid.findViewById(R.id.textViewQuantity);

        title.setText(names.get(position));
        textPrice.setText(price.get(position));
        textPrices.setText(prices.get(position));
        textSize.setText(size.get(position));
        textSizes.setText(sizes.get(position));
        textQuantity.setText(quantity.get(position));

        thumbNail.setImageUrl(images.get(position), imageLoader);

        return grid;
    }
}
