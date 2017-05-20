package com.zamora.fastoreapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zamora.fastoreapp.Clases.Producto;
import com.zamora.fastoreapp.ProductosListaActivity;
import com.zamora.fastoreapp.R;

import java.util.ArrayList;

import static com.zamora.fastoreapp.R.id.deleteIco;

/**
 * Created by Sergio on 13/04/2017.
 */

public class AdapterProductosCompra extends BaseAdapter implements Filterable {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected Activity activity;
    private static LayoutInflater inflater = null;

    protected ArrayList<Producto> originalItems;
    protected ArrayList<Producto> filteredItems;

    public AdapterProductosCompra(Activity activity, ArrayList<Producto> items) {
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.originalItems = items;
        this.filteredItems = items;
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            v = inflater.inflate(R.layout.item_producto_compra, null);
        }

        final Producto dir = filteredItems.get(position);

        TextView lblCantidad = (TextView) v.findViewById(R.id.lblCantidad);
        lblCantidad.setText(String.valueOf(dir.getCantidad()));

        final TextView nombre = (TextView) v.findViewById(R.id.productName);
        nombre.setText(dir.getNombre());

        TextView lblPrecio = (TextView) v.findViewById(R.id.lblPrecio);
        lblPrecio.setText(String.valueOf(dir.getPrecio()));

        ImageView deleteIco = (ImageView) v.findViewById(R.id.deleteIco);
        deleteIco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(dir.getNombre(), position);
            }
        });

        if (dir.getInCart()) {
            v.setBackgroundColor(Color.parseColor("#CFD8DC"));
        } else {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        v.setPadding(25,25,25,25);
        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0){
                    results.values = originalItems;
                    results.count = originalItems.size();
                }
                else{
                    String filterString = constraint.toString().toLowerCase();

                    ArrayList<Producto> filterResultsData = new ArrayList<>();
                    for (Producto data : originalItems){
                        if (data.getNombre().toLowerCase().contains(filterString)){
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (ArrayList<Producto>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    public AlertDialog confirmDelete(final String speechText, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Eliminar")
                .setMessage(speechText)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filteredItems.remove(position);
                        notifyDataSetChanged();
                        DatabaseReference refEliminar = database.getReference("Usuarios/"+ProductosListaActivity.nombreUser+"/Listas/"+ ProductosListaActivity.nombreLista+"/Detalle/"+speechText);
                        refEliminar.removeValue();
                        Toast.makeText(activity, "Eiminando Producto", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(activity, "Me ztaz vorrando karnal", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.show();

    }
}
