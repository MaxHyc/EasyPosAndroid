package com.devhyc.easypos.ui.articulos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTArticulo

class ItemRubroAdapter(var rubros:ArrayList<DTRubro>): RecyclerView.Adapter<ItemRubroAdapter.ItemRubroViewHolder>() {
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
        rubros.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRubroViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemRubroViewHolder(layoutInflater.inflate(R.layout.item_articulo,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemRubroViewHolder, position: Int) {
        val item: DTRubro = rubros[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = rubros.size

    class ItemRubroViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemArticuloBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTRubro)
        {
            try
            {
                binding.tvNombreArt.text = i.nombre
                binding.tvTipoDocDoc.text = "Código: ${i.codigo}"
                binding.tvTotalDoc.visibility = View.GONE
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