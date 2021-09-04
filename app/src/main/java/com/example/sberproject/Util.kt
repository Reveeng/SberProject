package com.example.sberproject

import com.example.sberproject.ui.articles.Article
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

object Util {
    val recyclingPlaces by lazy {
        listOf(
            RecyclingPlace(
                "Предприятие комплексного решения проблем промышленных отходов, ЕМУП",
                "Можно сдать: -ртутные лампы (люминесцентные и компактные энергосберегающие); ртутные термометры и другие ртутьсодержащие приборы; -малогабаритные источники тока (отработанные батарейки и аккумуляторы); -бытовую, компьютерную технику и оргтехнику. -отработанные автомобильные аккумуляторы.\n",
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
                "принимают ношеную чистую целую одежду (взрослую и детскую, обувь), игрушки, книги.",
                LatLng(56.822700129595944, 60.59999966912793),
                setOf(TrashType.PAPER, TrashType.CLOTHES, TrashType.OTHER)
            ),
            RecyclingPlace(
                "#НЕМУЗЕЙМУСОРА",
                "",
                LatLng(56.83990041333704, 60.5940305421064),
                setOf(
                    TrashType.PLASTIC,
                    TrashType.PAPER,
                    TrashType.CLOTHES,
                    TrashType.GLASS,
                    TrashType.METAL,
                    TrashType.APPLIANCES,
                    TrashType.TETRA_PACK,
                    TrashType.CAPS,
                    TrashType.OTHER
                )
            ),
            RecyclingPlace(
                "Экотранс",
                "принимают сырье от населения от 1 кг Пластик: пэт бутылки, кеги, упаковка от бытовой химии, мешки, канистры, бампера автомобильные и пподкрылки, канистры, ящики молочные Бумага: гофро картон, архивы, газеты. Пленка: ПВД, стрейч. Алюминиевая банка. Другое:деревянные поддоны, коробки из под бананов.",
                LatLng(56.870451054508095, 60.64722042492072),
                setOf(TrashType.PAPER, TrashType.PLASTIC, TrashType.METAL)
            ),
            RecyclingPlace(
                "Love Republic",
                "В контейнеры мы принимаем одежду, обувь, сумки и аксессуары кроме нижнего белья, носков и колготок. Пожалуйста, постирайте вещи перед сдачей!",
                LatLng(56.85297554287459, 60.55090837094209),
                setOf(TrashType.CLOTHES)
            ),
            RecyclingPlace(
                "Леруа Мерлен",
                "Принимают люминесцентные лампы, батарейки, светодиодные лампы.",
                LatLng(56.8293143834812, 60.51606056909486),
                setOf(TrashType.LAMPS, TrashType.BATTERIES)
            ),
            RecyclingPlace(
                "Аккумуляторный мир",
                "автомобильные и мотоциклетные аккумуляторы, дают скидку на покупку нового аккумулятора",
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

    val recyclingPlaceToTrashTypes by lazy {
        recyclingPlaces.map { it.name to it.trashTypes }.toMap()
    }

    val trashTypeToIcon by lazy {
        mapOf(
            TrashType.PAPER to R.drawable.trash_1,
            TrashType.PLASTIC to R.drawable.trash_3,
            TrashType.CLOTHES to R.drawable.trash_5,
            TrashType.APPLIANCES to R.drawable.trash_10,
            TrashType.GLASS to R.drawable.trash_2,
            TrashType.METAL to R.drawable.trash_4,
            TrashType.LAMPS to R.drawable.trash_9,
            TrashType.HAZARDOUS_WASTE to R.drawable.trash_7,
            TrashType.BATTERIES to R.drawable.trash_8,
            TrashType.TETRA_PACK to R.drawable.trash_11,
            TrashType.CAPS to R.drawable.trash_12,
            TrashType.TIRES to R.drawable.trash_13,
            TrashType.OTHER to R.drawable.trash_6
        )
    }

    val trashTypeToMarker by lazy {
        mapOf(
            setOf(TrashType.PAPER) to R.drawable.marker_1,
            setOf(TrashType.PLASTIC) to R.drawable.marker_3,
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
            setOf(
                TrashType.PAPER,
                TrashType.GLASS,
                TrashType.PLASTIC,
                TrashType.METAL,
                TrashType.CLOTHES,
                TrashType.OTHER,
                TrashType.APPLIANCES,
                TrashType.TETRA_PACK,
                TrashType.CAPS
            ) to R.drawable.marker_1_2_3_4_5_6_10_11_12,
            setOf(
                TrashType.PAPER,
                TrashType.GLASS,
                TrashType.PLASTIC,
                TrashType.METAL,
                TrashType.CLOTHES,
                TrashType.OTHER,
                TrashType.BATTERIES,
                TrashType.APPLIANCES,
                TrashType.CAPS
            ) to R.drawable.marker_1_2_3_4_5_6_8_10_12,
            setOf(TrashType.PAPER, TrashType.PLASTIC) to R.drawable.marker_1_3,
            setOf(TrashType.PAPER, TrashType.PLASTIC, TrashType.CLOTHES) to R.drawable.marker_1_3_5,
            setOf(
                TrashType.PAPER,
                TrashType.PLASTIC,
                TrashType.OTHER,
                TrashType.CAPS
            ) to R.drawable.marker_1_3_6_12,
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
            setOf(TrashType.BATTERIES, TrashType.LAMPS) to R.drawable.marker_8_9,
            setOf(
                TrashType.PAPER,
                TrashType.OTHER,
                TrashType.HAZARDOUS_WASTE,
                TrashType.BATTERIES,
                TrashType.LAMPS,
                TrashType.APPLIANCES
            ) to R.drawable.marker_1_6_7_8_9_10,
            setOf(
                TrashType.PAPER,
                TrashType.OTHER,
                TrashType.HAZARDOUS_WASTE,
                TrashType.BATTERIES,
                TrashType.LAMPS,
                TrashType.APPLIANCES,
                TrashType.TIRES
            ) to R.drawable.marker_1_6_7_8_9_10_13,
            setOf(TrashType.PLASTIC, TrashType.CAPS) to R.drawable.marker_3_12,
            setOf(TrashType.CAPS) to R.drawable.marker_12
        )
    }

    val articles by lazy {
        listOf(
            Article(
                "https://www.ecodao.ru/what-is-ecology-adout/",
                "О чём наука экология?",
                "Интересно и просто расскажем, что такое экология и о чем эта наука?",
                "https://lh5.googleusercontent.com/TbDj_27N-EsC6S2aLHtXh48AZlSVgUSHq4aPmyNgLo9M5J0KLXul8rLlypB7kqTTH87bYt-A6Rgp-Sln2a3SADCphSWRRndogi1YPEAzCQQ6Pd02__Ps-ubuOdLH-KBOKe9ReXc"
            ),
            Article(
                "https://trends.rbc.ru/trends/green/5d696a8c9a7947741b7e954d",
                "Как сортировать мусор дома. Краткая инструкция",
                "Расскажем, как ввести эту полезную привычку в повседневную жизнь",
                "https://s0.rbk.ru/v6_top_pics/resized/590xH/media/img/7/03/755851506080037.png"
            ),
            Article(
                "https://nplus1.ru/material/2018/03/22/landfill-gases",
                "Химическая жизнь мусора",
                "Что происходит с отходами, когда они попадают на свалку?",
                "https://nplus1.ru/images/2018/03/22/d8f0749839e914124c13c643c3321bb6.jpg"
            )
        )
    }

    val markerToRecyclingPlace by lazy {
        mutableMapOf<Marker, RecyclingPlace>()
    }
}