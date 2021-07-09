package com.example.sberproject

import com.google.android.gms.maps.model.LatLng

object Util {
    val recyclingPlaces by lazy {
        listOf(
            RecyclingPlace(
                "Предприятие комплексного решения проблем промышленных отходов, ЕМУП",
                LatLng(56.83770976967497, 60.60828874594135),
                setOf(
                    TrashType.LAMPS,
                    TrashType.BATTERIES,
                    TrashType.HAZARDOUS_WASTE,
                    TrashType.OTHER
                )
            ),
            RecyclingPlace(
                "Ново-Тихвинский женский монастырь",
                LatLng(56.822700129595944, 60.59999966912793),
                setOf(TrashType.PAPER, TrashType.CLOTHES, TrashType.OTHER)
            ),
            RecyclingPlace(
                "#НЕМУЗЕЙМУСОРА",
                LatLng(56.83990041333704, 60.5940305421064),
                setOf(
                    TrashType.PLASTIC,
                    TrashType.PAPER,
                    TrashType.CLOTHES,
                    TrashType.GLASS,
                    TrashType.METAL,
                    TrashType.APPLIANCES,
                    TrashType.TETRA_PACK,
                    TrashType.OTHER
                )
            ),
            RecyclingPlace(
                "Экотранс",
                LatLng(56.870451054508095, 60.64722042492072),
                setOf(TrashType.PAPER, TrashType.PLASTIC, TrashType.METAL)
            ),
            RecyclingPlace(
                "Love Republic",
                LatLng(56.85297554287459, 60.55090837094209),
                setOf(TrashType.CLOTHES)
            ),
            RecyclingPlace(
                "Леруа Мерлен",
                LatLng(56.8293143834812, 60.51606056909486),
                setOf(TrashType.LAMPS, TrashType.BATTERIES)
            ),
            RecyclingPlace(
                "Аккумуляторный мир",
                LatLng(56.830657070261054, 60.632205642105994),
                setOf(TrashType.HAZARDOUS_WASTE, TrashType.OTHER)
            )
        )
    }

    val trashTypeToRecyclerPlaces by lazy {
        val result = TrashType.values().map { it to mutableListOf<RecyclingPlace>() }.toMap()
        recyclingPlaces.map { rp -> rp.trashTypes.map { result[it]?.add(rp) } }
        result
    }

    val trashTypeToMarker by lazy {
        mapOf(
            setOf(TrashType.PAPER) to R.drawable.marker_1,
            setOf(TrashType.GLASS) to R.drawable.marker_3,
            setOf(TrashType.METAL) to R.drawable.marker_4,
            setOf(TrashType.CLOTHES) to R.drawable.marker_5,
            setOf(TrashType.OTHER) to R.drawable.marker_6,
            setOf(TrashType.BATTERIES) to R.drawable.marker_8,
            setOf(
                TrashType.PAPER,
                TrashType.GLASS,
                TrashType.PLASTIC,
                TrashType.METAL,
                TrashType.APPLIANCES
            ) to R.drawable.marker_1_2_3_4_10,
            setOf(
                TrashType.PAPER,
                TrashType.GLASS,
                TrashType.PLASTIC,
                TrashType.METAL,
                TrashType.CLOTHES,
                TrashType.OTHER
            ) to R.drawable.marker_1_2_3_4_5_6,
            setOf(
                TrashType.PAPER,
                TrashType.GLASS,
                TrashType.PLASTIC,
                TrashType.METAL,
                TrashType.CLOTHES,
                TrashType.OTHER,
                TrashType.APPLIANCES
            ) to R.drawable.marker_1_2_3_4_5_6_10,
            setOf(TrashType.PAPER, TrashType.PLASTIC) to R.drawable.marker_1_3,
            setOf(TrashType.PAPER, TrashType.PLASTIC, TrashType.METAL) to R.drawable.marker_1_3_4,
            setOf(
                TrashType.PAPER,
                TrashType.PLASTIC,
                TrashType.METAL,
                TrashType.OTHER,
                TrashType.APPLIANCES
            ) to R.drawable.marker_1_3_4_6_10,
            setOf(TrashType.PAPER, TrashType.CLOTHES, TrashType.OTHER) to R.drawable.marker_1_5_6,
            setOf(TrashType.CLOTHES, TrashType.OTHER) to R.drawable.marker_5_6,
            setOf(
                TrashType.CLOTHES,
                TrashType.OTHER,
                TrashType.APPLIANCES
            ) to R.drawable.marker_5_6_10,
            setOf(TrashType.CLOTHES, TrashType.BATTERIES) to R.drawable.marker_5_8,
            setOf(TrashType.OTHER, TrashType.HAZARDOUS_WASTE) to R.drawable.marker_6_7,
            setOf(
                TrashType.OTHER,
                TrashType.HAZARDOUS_WASTE,
                TrashType.BATTERIES,
                TrashType.LAMPS
            ) to R.drawable.marker_6_7_8_9,
            setOf(
                TrashType.OTHER,
                TrashType.HAZARDOUS_WASTE,
                TrashType.LAMPS
            ) to R.drawable.marker_6_7_9,
            setOf(TrashType.OTHER, TrashType.BATTERIES) to R.drawable.marker_6_8,
            setOf(
                TrashType.HAZARDOUS_WASTE,
                TrashType.BATTERIES,
                TrashType.LAMPS
            ) to R.drawable.marker_7_8_9,
            setOf(TrashType.BATTERIES, TrashType.LAMPS) to R.drawable.marker_8_9
        )
    }
}