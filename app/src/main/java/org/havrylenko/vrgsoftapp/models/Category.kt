package org.havrylenko.vrgsoftapp.models

data class Category(
    val id: String,
    val name: String,
    var isSelected: Boolean = false
)