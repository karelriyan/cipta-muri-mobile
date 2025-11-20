package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R

class RiwayatSetoranAdapter(
    private var items: List<RiwayatSetoran>,
    private val onItemClick: ((RiwayatSetoran) -> Unit)? = null
) : RecyclerView.Adapter<RiwayatSetoranAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvSaldo: TextView = view.findViewById(R.id.tvSaldo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_setoran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvTanggal.text = item.tanggal
        holder.tvJudul.text = item.beratFormatted
        holder.tvSaldo.text = item.totalSaldoFormatted

        holder.itemView.setOnClickListener { onItemClick?.invoke(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<RiwayatSetoran>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
