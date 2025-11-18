package com.main.cipta_muri_mobile.data.news

import com.google.gson.annotations.SerializedName

data class NewsItem(
    @SerializedName(value = "title", alternate = ["judul", "name"])
    val title: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName(value = "excerpt", alternate = ["ringkasan", "subtitle", "deskripsi", "description"])
    val excerpt: String? = null,
    @SerializedName(value = "published_at", alternate = ["created_at", "tanggal", "updated_at", "date", "tanggal_publish", "publishedAt"])
    val publishedAt: String? = null,
    @SerializedName(
        value = "thumbnail",
        alternate = [
            "image",
            "gambar",
            "thumbnail_url",
            "cover",
            "featured_image",
            "featured_image_url",
            "image_url",
            "cover_url"
        ]
    )
    val thumbnailUrl: String? = null
)
