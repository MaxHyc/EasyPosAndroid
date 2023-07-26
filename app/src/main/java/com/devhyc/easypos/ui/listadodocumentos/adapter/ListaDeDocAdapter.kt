package com.devhyc.easypos.ui.listadodocumentos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocLista
import com.devhyc.easypos.databinding.ItemListadoDocBinding

class ListaDeDocAdapter (var documentos:ArrayList<DTDocLista>): RecyclerView.Adapter<ListaDeDocAdapter.ItemDocViewHolder>() {
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
        documentos.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDocViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemDocViewHolder(layoutInflater.inflate(R.layout.item_listado_doc,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemDocViewHolder, position: Int) {
        val item: DTDocLista = documentos[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = documentos.size

    class ItemDocViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemListadoDocBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTDocLista)
        {
            try
            {
                binding.tvVistaNroDoc.text = "Nro Doc: ${i.NroDoc.toString()}"
                binding.tvVistaMonedaSignoDoc.text = i.MonedaSigno
                binding.tvVistaTotalDoc.text = i.Total.toString()
                binding.tvVistaTipoDocCod.text = "Tipo Doc: ${i.TipoDocCodigo}"
                //
                if (i.SerieCfe.isNotEmpty())
                {
                    binding.tvVistaSerieCFE.text = "Serie CFE: ${i.SerieCfe}"
                    binding.tvVistaSerieCFE.visibility = View.VISIBLE
                }
                if (i.TipoCfeNombre.isNotEmpty())
                {
                    binding.tvVistaTipoCFENombre.text = "Nombre CFE: ${i.TipoCfeNombre}"
                    binding.tvVistaTipoCFENombre.visibility = View.VISIBLE
                }
                if (i.NroCfe.compareTo(4) == 0)
                {
                    binding.tvVistaNroCFE.text = "Nro CFE: ${i.NroCfe.toString()}"
                    binding.tvVistaNroCFE.visibility = View.VISIBLE
                }
                if (i.ClienteNombre != "")
                {
                    binding.tvVistaClienteNombre.text = i.ClienteNombre
                    binding.tvVistaClienteNombre.visibility = View.VISIBLE
                }
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