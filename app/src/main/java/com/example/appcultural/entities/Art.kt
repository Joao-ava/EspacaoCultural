package com.example.appcultural.entities

data class Art(
    public var id: String = "",
    public val name: String = "",
    public val publishDate: Int = 2000,
    public val description: String = "",
    public val author: String = "",
    public val isActive: Boolean = true,
    public val imageUrl: String = "",
    public val genders: List<String> = listOf(),
    public val location: ArtLocation = ArtLocation()
)
