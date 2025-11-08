package com.main.cipta_muri_mobile.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R
import de.hdodenhof.circleimageview.CircleImageView

class LeaderboardAdapter(private val items: List<LeaderboardItem>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: CircleImageView = view.findViewById(R.id.iv_user_avatar)
        val tvName: TextView = view.findViewById(R.id.tv_user_name)
        val tvPoints: TextView = view.findViewById(R.id.tv_user_points) // optional
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_rank, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Isi data ke tampilan
        holder.tvName.text = item.name
        holder.tvPoints.text = item.points.toString()

        // Avatar default (gunakan drawable/avatar)
        holder.ivAvatar.setImageResource(R.drawable.avatar)

        // (Opsional) Jika kamu mau ubah border warna berdasarkan rank
        when (item.rank) {
            "1" -> holder.ivAvatar.borderColor =
                holder.itemView.context.getColor(R.color.gold)
            "2" -> holder.ivAvatar.borderColor =
                holder.itemView.context.getColor(R.color.silver)
            "3" -> holder.ivAvatar.borderColor =
                holder.itemView.context.getColor(R.color.bronze)
            else -> holder.ivAvatar.borderColor =
                holder.itemView.context.getColor(R.color.gray)
        }
    }

    override fun getItemCount() = items.size
}
