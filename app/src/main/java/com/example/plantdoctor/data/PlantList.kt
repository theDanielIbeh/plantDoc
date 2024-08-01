package com.example.plantdoctor.data

import com.example.plantdoctor.data.entities.plant.Plant


object PlantList {
    fun list(): List<Plant> =
        arrayListOf(
            Plant(
                id = 1,
                name = "Tomato",
                imageUrl="https://plantvillage-production-new.s3.amazonaws.com/image/1200/file/medium-2c50f2ab34ec579ecbd4b8348c3796e3.jpg",
                botanicalName = "Lycopersicon esculentum",
                generalInfo = "Tomato is an herbaceous annual in the family Solanaceae grown for its edible fruit. The plant can be erect with short stems or vine-like with long, spreading stems.\n\n" +
                        "The stems are covered in coarse hairs and the leaves are arranged spirally. The tomato plant produces yellow flowers, which can develop into a cyme of 3–12, and usually, a round fruit (berry) that is fleshy, smoothed skin, and can be red, pink, purple, brown, orange, or yellow in color.\n\n" +
                        "The tomato plant can grow 0.7–2 m (2.3–6.6 ft) in height and as an annual, is harvested after only one growing season. Tomato may also be referred to as the love apple and originates from South America.\n\n" +
                        "Tomatoes are native to South and Central America, but they are now grown all over the world.\n\n" +
                        "Tomatoes are one of Africa's most widely grown vegetable crops. They are grown for home consumption in almost every homestead's backyard across Sub-Saharan Africa.\n\n" +
                        "They are a good source of vitamins as well as a cash crop for smallholders and medium-scale commercial farmers. Tomatoes used as flavor enhancers in food are always in high demand, both fresh and processed."
            )
        )
}