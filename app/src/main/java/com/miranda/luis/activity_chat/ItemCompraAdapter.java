package com.miranda.luis.activity_chat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemCompraAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<ItemCompra> items;
	
	
	public ItemCompraAdapter(Activity activity, ArrayList<ItemCompra> items) {
		this.activity = activity;
		this.items = items;
	}


	@Override
	public int getCount() {
		return items.size();
	}


	@Override
	public Object getItem(int position) {
		return items.get(position);
	}


	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		
        if(convertView == null) {
        	LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	vi = inflater.inflate(R.layout.list_item_layout, null);
        }
            
        ItemCompra item = items.get(position);
        
        ImageView image = (ImageView) vi.findViewById(R.id.imagen);
        int imageResource = activity.getResources().getIdentifier(item.getRutaImagen(), null, activity.getPackageName());
        image.setImageDrawable(activity.getResources().getDrawable(imageResource));

		ImageView stat = (ImageView) vi.findViewById(R.id.status);
		int imageResources = activity.getResources().getIdentifier(item.getStatus(), null, activity.getPackageName());
		stat.setImageDrawable(activity.getResources().getDrawable(imageResources));
        
        TextView nombre = (TextView) vi.findViewById(R.id.nombre);
        nombre.setText(item.getNombre());
        
        TextView tipo = (TextView) vi.findViewById(R.id.tipo);
        tipo.setText(item.getTipo());

        return vi;
	}
}
