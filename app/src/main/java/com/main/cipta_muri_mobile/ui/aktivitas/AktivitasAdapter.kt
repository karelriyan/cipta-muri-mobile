package com.main.cipta_muri_mobile.ui.aktivitas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.Aktivitas

class AktivitasAdapter(private val listAktivitas: List<Aktivitas>) :
    RecyclerView.Adapter<AktivitasAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvJenis: TextView = view.findViewById(R.id.tvJenisTransaksi)
        val tvKeterangan: TextView = view.findViewById(R.id.tvKeterangan)
        val tvJumlah: TextView = view.findViewById(R.id.tvJumlah)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aktivitas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val aktivitas = listAktivitas[position]
        holder.tvTanggal.text = aktivitas.tanggal
        holder.tvJenis.text = aktivitas.jenis
        holder.tvKeterangan.text = aktivitas.keterangan
        holder.tvJumlah.text = aktivitas.jumlah

        if (aktivitas.isMasuk) {
            holder.tvJumlah.setTextColor(holder.itemView.context.getColor(R.color.green_500))
            holder.ivIcon.setImageResource(R.drawable.ic_money_in)
        } else {
            holder.tvJumlah.setTextColor(holder.itemView.context.getColor(R.color.red_500))
            holder.ivIcon.setImageResource(R.drawable.ic_money_in)
            holder.ivIcon.rotation = 180f // panah dibalik ke atas
        }
    }

    override fun getItemCount(): Int = listAktivitas.size
}
