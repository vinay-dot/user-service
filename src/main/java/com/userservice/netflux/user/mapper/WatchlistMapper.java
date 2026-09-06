package com.userservice.netflux.user.mapper;

import java.util.List;

import com.userservice.netflux.user.entity.WatchlistItem;

public class WatchlistMapper {

    private WatchlistMapper() {
    }

    public static WatchlistItem toEntity(Long userId, Long movieId) {
        var item = new WatchlistItem();
        item.setUserId(userId);
        item.setMovieId(movieId);
        return item;
    }

    public static List<Long> toMovieIds(List<WatchlistItem> items) {
        return items.stream()
                .map(WatchlistItem::getMovieId)
                .toList();
    }
}
