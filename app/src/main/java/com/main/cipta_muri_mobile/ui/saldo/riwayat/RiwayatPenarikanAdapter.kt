package com.main.cipta_muri_mobile.ui.saldo.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.main.cipta_muri_mobile.R

class RiwayatPenarikanAdapter(private var items: List<RiwayatPenarikanItem>) :
    RecyclerView.Adapter<RiwayatPenarikanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val tvNominal: TextView = view.findViewById(R.id.tvNominal)
        val cardView: MaterialCardView = view.findViewById(R.id.cardRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tukar_poin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTanggal.text = item.tanggal
        holder.tvJudul.text = item.judul
        holder.tvSubtitle.text = item.subtitle
        holder.tvNominal.text = item.nominal
        holder.tvNominal.setTextColor(item.warnaNominal)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<RiwayatPenarikanItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
