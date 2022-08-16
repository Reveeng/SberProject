package com.example.sberproject.ui.map

import android.graphics.Color

class TrashItem {
    var icons: Int ?= 0
    var type:String ?= null
    var color: Int ?= Color.BLACK

    constructor(icons: Int?, type:String?, color:Int?){
        this.color = color
        this.icons = icons
        this.type = type
    }
}