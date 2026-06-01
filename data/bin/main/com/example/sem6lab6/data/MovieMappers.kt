package com.example.sem6lab6.data

import com.example.sem6lab6.domain.model.Movie

internal fun MovieEntity.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        genre = genre,
        year = year,
        durationMinutes = durationMinutes,
        rating = rating,
        description = description,
        isFavorite = isFavorite,
        isWatched = isWatched
    )
}

internal fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        genre = genre,
        year = year,
        durationMinutes = durationMinutes,
        rating = rating,
        description = description,
        isFavorite = isFavorite,
        isWatched = isWatched
    )
}
