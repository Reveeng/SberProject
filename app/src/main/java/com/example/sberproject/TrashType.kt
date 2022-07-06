package com.example.sberproject

enum class TrashType {
    PAPER, PLASTIC, CLOTHES, APPLIANCES, GLASS, METAL, LAMPS, HAZARDOUS_WASTE, BATTERIES, TETRA_PACK, CAPS, TIRES, OTHER;

    fun toStringUI(): String {
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

        private val stringToTrashType by lazy {
            trashTypeToString.map { it.value to it.key }.toMap()
        }

        fun fromInt(value: Int) = TrashType.values().first { it.ordinal == value }

        fun fromString(value: String) =
            stringToTrashType[value] ?: throw Exception("Unknown trash type")
    }
}