package com.vustuntas.myfavoriteplaces.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vustuntas.myfavoriteplaces.model.Places
import com.vustuntas.myfavoriteplaces.R
import com.vustuntas.myfavoriteplaces.view.MainActivity
import com.vustuntas.myfavoriteplaces.view.MapsActivity

class RecyclerAdapterPlace(var placeObject : List<Places>) : RecyclerView.Adapter<RecyclerAdapterPlace.PlaceVH>() {
    private var singletonClass  =SingletonClass.place
    class PlaceVH( itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row,parent,false)
        return PlaceVH(itemView)
    }

    override fun onBindViewHolder(holder: PlaceVH, position: Int) {
        val obje = placeObject.get(position) as Places
        holder.itemView.findViewById<TextView>(R.id.recyclerView_placeNameTextView).text = obje.name.toString()
        holder.itemView.setOnClickListener {
            singletonClass.obje = placeObject.get(position)
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("recycler","recycler")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            holder.itemView.context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return placeObject.size
    }
}