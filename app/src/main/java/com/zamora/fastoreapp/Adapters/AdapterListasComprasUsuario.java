package com.zamora.fastoreapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zamora.fastoreapp.Clases.ListaCompras;
import com.zamora.fastoreapp.Clases.Usuario;
import com.zamora.fastoreapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;
import static com.google.android.gms.common.stats.zzc.zza.El;
import static com.zamora.fastoreapp.R.id.fechaLista;

/**
 * Created by Dell on 3/1/2017.
 */

public class AdapterListasComprasUsuario extends BaseAdapter implements Filterable{

    protected Activity activity;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static LayoutInflater inflater = null;
    private String idUsuario;
    protected ArrayList<ListaCompras> originalItems;
    protected ArrayList<ListaCompras> filteredItems;

    public AdapterListasComprasUsuario(Activity activity, ArrayList<ListaCompras> items, String idUsuario) {
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.idUsuario = idUsuario;
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
            v = inflater.inflate(R.layout.item_lista_compras, null);
        }

        final ListaCompras dir = filteredItems.get(position);

        TextView title = (TextView) v.findViewById(R.id.nombreLista);
        title.setText(dir.getNombre());

        final TextView fechaLista = (TextView) v.findViewById(R.id.fechaLista);
        if (dir.getIdUsuario().equals(idUsuario)) {
            fechaLista.setText(dir.getFechaCompra());
        } else {

            final DatabaseReference refU = database.getReference("Usuarios");
            refU.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot us = dataSnapshot.child(dir.getIdUsuario());
                    boolean x = us.exists();
                    if(x){
                        DataSnapshot info = us.child("Informacion");
                        String correito = info.getValue(Usuario.class).getEmail();
                        System.out.println("El correo del dueño es " + correito);
                        fechaLista.setText(correito);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //v.setBackgroundColor(Color.parseColor("#3f834D"));
        v.setPadding(50,50,50,50);
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

                    ArrayList<ListaCompras> filterResultsData = new ArrayList<>();
                    for (ListaCompras data : originalItems){
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
                filteredItems = (ArrayList<ListaCompras>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
