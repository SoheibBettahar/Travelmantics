package com.example.travelmantics

import java.io.Serializable


data class TravelDeal(
    var title: String = "", var price: String = "", var description: String = "",
    var imageUrl: String = "",
    var imageName: String = ""
) : Serializable {
    var id: String = ""

    constructor() : this(title = "", price = "", description = "", imageUrl = "",imageName = "")
}