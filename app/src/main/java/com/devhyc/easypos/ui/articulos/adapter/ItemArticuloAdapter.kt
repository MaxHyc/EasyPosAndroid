package com.devhyc.easymanagementmobile.ui.articulos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTArticulo

class ItemArticuloAdapter (var articulos:ArrayList<DTArticulo>): RecyclerView.Adapter<ItemArticuloAdapter.ItemArticuloViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener
    {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener)
    {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int)
    {
        articulos.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemArticuloViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemArticuloViewHolder(layoutInflater.inflate(R.layout.item_articulo,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemArticuloViewHolder, position: Int) {
        val item: DTArticulo = articulos[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = articulos.size

    class ItemArticuloViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemArticuloBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTArticulo)
        {
            try
            {
                binding.tvNombreArt.text = i.nombre
                if (Globales.ParametrosDocumento != null)
                {
                    if(Globales.ParametrosDocumento.Validaciones.Valorizado)
                    {
                        binding.tvTotalDoc.text = "${i.monedaSigno} ${i.precioFinal}"
                    }
                    else
                    {
                        binding.tvTotalDoc.text = ""
                    }
                }
                else
                {
                    binding.tvTotalDoc.text=""
                }
                binding.tvFechaDoc.text = "Familia: ${i.familianombre}"
                binding.tvTipoDocDoc.text = "Código: ${i.codigo}"
                binding.tvSerie.text = ""
            }
            catch (e: Exception)
            {
                throw IllegalArgumentException(e.message)
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}