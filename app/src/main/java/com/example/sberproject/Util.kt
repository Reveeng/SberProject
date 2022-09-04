package com.example.sberproject

import com.example.sberproject.ui.articles.Article
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

object Util {
    val trashTypesForInstruction by lazy {
        setOf(TrashType.PLASTIC)
    }

    val cityNames by lazy {
        mapOf(
            "Екатеринбург" to "ekaterinburg"
        )
    }

    val trashTypeToColor by lazy {
        mapOf(
            TrashType.PAPER to R.color.paperColor,
            TrashType.PLASTIC to R.color.plasticColor,
            TrashType.CLOTHES to R.color.clothesColor,
            TrashType.APPLIANCES to R.color.appliancesColor,
            TrashType.GLASS to R.color.glassColor,
            TrashType.METAL to R.color.metalColor,
            TrashType.LAMPS to R.color.lampsColor,
            TrashType.HAZARDOUS_WASTE to R.color.hazardousWasteColor,
            TrashType.BATTERIES to R.color.batteriesColor,
            TrashType.TETRA_PACK to R.color.tetrapackColor,
            TrashType.CAPS to R.color.capsColor,
            TrashType.TIRES to R.color.tiresColor,
            TrashType.OTHER to R.color.otherColor
        )
    }

    val trashTypeToIcon by lazy {
        mapOf(
            TrashType.PAPER to R.drawable.ic_paper,
            TrashType.PLASTIC to R.drawable.ic_plastic_botlle,
            TrashType.CLOTHES to R.drawable.ic_clothes,
            TrashType.APPLIANCES to R.drawable.ic_appliances,
            TrashType.GLASS to R.drawable.ic_glass,
            TrashType.METAL to R.drawable.ic_metall,
            TrashType.LAMPS to R.drawable.ic_lamp,
            TrashType.HAZARDOUS_WASTE to R.drawable.ic_dangerous,
            TrashType.BATTERIES to R.drawable.ic_battery,
            TrashType.TETRA_PACK to R.drawable.ic_tetrapack,
            TrashType.CAPS to R.drawable.ic_cap,
            TrashType.TIRES to R.drawable.ic_tires,
            TrashType.OTHER to R.drawable.ic_other
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

    val recyclingPlaces by lazy {
        listOf(
            RecyclingPlace(
                "#НЕМУЗЕЙМУСОРА",
                "",
                LatLng(56.84007145449412, 60.59386779098514),
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
                )
            ),
            RecyclingPlace(
                "Вещь добра",
                "Возле ТЦ Европа стоит бокс Вещь добра, куда можно сдать одежду, обувь и сумки в любом состоянии.\r\nОбратите внимание, что не принимается нижнее белье, колготки, носки.\r\nБолее подробную информацию можно найти: https://vk.com/vesh.dobra, https://www.instagram.com/vesh.dobra/?hl=ru",
                LatLng(56.83809000000008, 60.59507000000008),
                setOf(TrashType.CLOTHES)
            ),
            RecyclingPlace(
                "Книгообмен",
                "Стеллаж для обмена книгами.",
                LatLng(56.839519979823166, 60.598710011969644),
                setOf(TrashType.PAPER)
            ),
            RecyclingPlace(
                "Благомаркет",
                "Магазин Благомаркет принимаем женские одежду, обувь, аксессуары в хорошем состоянии, чистые, желательно из актуальных коллекций. Важно, что эта одежда будет потом перепродаваться, поэтому просим тех, кто сдает нам вещи, задаться вопросом: «А отдала бы я это подруге?» Вырученные от продаж деньги передаются на благотворительность.\r\n\r\nПравила сдачи вещей:  http://blagomarket.store/resale\r\n\r\nИнстаграм: https://www.instagram.com/blagomarket/",
                LatLng(56.845030000000065, 60.591710000000035),
                setOf(TrashType.CLOTHES)
            ),
            RecyclingPlace(
                "Бокс для сбора батареек в магазине Ель",
                "2 этаж",
                LatLng(56.8450242228236, 60.59060153961184),
                setOf(TrashType.BATTERIES)
            ),
            RecyclingPlace(
                "Вещь добра",
                "В Дворце игровых видов спорта стоит бокс Вещь добра, куда можно сдать одежду, обувь и сумки в любом состоянии.\r\nОбратите внимание, что не принимается нижнее белье, колготки, носки.\r\nБолее подробную информацию можно найти: https://vk.com/vesh.dobra, https://www.instagram.com/vesh.dobra/?hl=ru",
                LatLng(56.847660000000076, 60.597290000000044),
                setOf(TrashType.CLOTHES)
            )
        )
    }

    val articles by lazy {
        listOf(
            Article(
                "https://ecowiki.ru/protivopozharnye-mery-chast-2/",
                "Противопожарные меры: системные изменения для предотвращения пожаров. Часть 2",
                "https://ecowiki.ru/wp-content/uploads/2021/09/image2-1.jpg"
            ),
            Article(
                "https://ecowiki.ru/protivopozharnye-mery-chast-1/",
                "Противопожарные меры: системные изменения для предотвращения пожаров. Часть 1",
                "https://ecowiki.ru/wp-content/uploads/2021/09/image1-1.jpg"
            ),
            Article(
                "https://ecowiki.ru/osennij-konkurs-volonterskih-posadok-2021/",
                "Осенний конкурс волонтерских посадок леса – 2021",
                "https://ecowiki.ru/wp-content/uploads/2021/09/1DJI_0095.jpg"
            ),
            Article(
                "https://ecowiki.ru/eko-znachit-ekonomiya-na-kakih-zelenyh-resheniyah-mozhet-vyigrat-restorannyj-biznes-chast-2/",
                "Эко – значит «экономия». На каких «зеленых» решениях может выиграть ресторанный бизнес. Часть 2",
                "https://ecowiki.ru/wp-content/uploads/2021/09/Veranda_Bjorn-4-.jpg"
            ),
            Article(
                "https://ecowiki.ru/eko-znachit-ekonomiya-na-kakih-zelenyh-resheniyah-mozhet-vyigrat-restorannyj-biznes-chast-1/",
                "Эко – значит «экономия». На каких «зеленых» решениях может выиграть ресторанный бизнес. Часть 1",
                "https://ecowiki.ru/wp-content/uploads/2021/09/image1.jpg"
            ),
            Article(
                "https://ecowiki.ru/luchshie-studencheskie-ekoproekty-rossii-chast-3/",
                "Лучшие студенческие экопроекты России. Часть 3",
                "https://ecowiki.ru/wp-content/uploads/2021/08/image16.jpg"
            ),
            Article(
                "https://ecowiki.ru/luchshie-studencheskie-ekoproekty-rossii-chast-2/",
                "Лучшие студенческие экопроекты России. Часть 2",
                "https://ecowiki.ru/wp-content/uploads/2021/08/image17.jpg"
            ),
            Article(
                "https://ecowiki.ru/kak-stat-zapovednym-volonterom/",
                "Как стать «заповедным волонтером»",
                "https://ecowiki.ru/wp-content/uploads/2021/08/image1-8.jpg"
            ),
            Article(
                "https://ecowiki.ru/pozhary-v-yakutii-chem-mozhno-pomoch/",
                "Пожары в Якутии: чем можно помочь?",
                "https://ecowiki.ru/wp-content/uploads/2021/08/image1-7.jpg"
            ),
            Article(
                "https://ecowiki.ru/kak-prozhit-uchebnyj-god-ekologichno/",
                "Как прожить учебный год экологично",
                "https://ecowiki.ru/wp-content/uploads/2021/08/blackboard-3651948.png"
            )
        )
    }

    val markerToRecyclingPlace by lazy {
        mutableMapOf<Marker, RecyclingPlace>()
    }
}