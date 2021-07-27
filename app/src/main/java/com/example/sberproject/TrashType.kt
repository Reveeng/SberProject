package com.example.sberproject

enum class TrashType {
    PAPER, PLASTIC, CLOTHES, APPLIANCES, GLASS, METAL, LAMPS, HAZARDOUS_WASTE, BATTERIES, TETRA_PACK, CAPS, TIRES, OTHER;

    override fun toString(): String {
        return trashTypeToString[this] ?: throw Exception("Unknown trash type")
    }

    companion object {
        private val trashTypeToString by lazy {
            mapOf(
                PAPER to "Бумага",
                PLASTIC to "Пластик",
                CLOTHES to "Одежда",
                APPLIANCES to "Бытовая\nтехника",
                GLASS to "Стекло",
                METAL to "Металл",
                LAMPS to "Лампочки",
                HAZARDOUS_WASTE to "Опасные\nотходы",
                BATTERIES to "Батарейки",
                TETRA_PACK to "Тетра\nПак",
                CAPS to "Крышечки",
                TIRES to "Шины",
                OTHER to "Иное"
            )
        }
    }
}