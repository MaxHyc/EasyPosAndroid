package com.devhyc.easypos.ui.articulos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.AndroidEntryPoint

class ArticuloAdapter (var articulos:List<DTArticulo>): RecyclerView.Adapter<ArticuloAdapter.RubroViewHolder>()
{
    lateinit var mListener: onItemClickListener

    interface onItemClickListener
    {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener)
    {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RubroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RubroViewHolder(layoutInflater.inflate(R.layout.item_articulo,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: RubroViewHolder, position: Int) {
        val item: DTArticulo = articulos[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = articulos.size

    class RubroViewHolder(view: View, listener: onItemClickListener):RecyclerView.ViewHolder(view) {
        private val binding = ItemArticuloBinding .bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(rub: DTArticulo)
        {
            binding.tvNombreArticulo.text = rub.nombre
            //binding.tvCodigoBarraArticulo.text = rub.codigoBarras
            //binding.tvFamiliaArticulo.text = rub.familianombre
            binding.tvPrecioArticulo.text= "${rub.monedaSigno} ${rub.precioFinal}"
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}