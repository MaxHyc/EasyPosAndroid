package com.devhyc.easypos.ui.mediospagos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTBanco

class customSpinnerBancos (var context: Context, var bancos:ArrayList<DTBanco>) : BaseAdapter() {
    override fun getCount(): Int {
        return bancos.count()
    }

    override fun getItem(position: Int): DTBanco {
        return bancos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var  view= LayoutInflater.from(context).inflate(R.layout.item_lista_precio,parent,false)
        var item: TextView =view.findViewById<TextView>(R.id.tvListaPre)
        item.text = bancos[position].Nombre
        return view
    }
}