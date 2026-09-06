package com.userservice.netflux.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userservice.netflux.user.mapper.WatchlistMapper;
import com.userservice.netflux.user.repository.WatchlistRepository;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public List<Long> getWatchlist(Long userId) {
        return WatchlistMapper.toMovieIds(watchlistRepository.findByUserId(userId));
    }

    @Transactional
    public void addToWatchlist(Long userId, Long movieId) {
        if (watchlistRepository.findByUserIdAndMovieId(userId, movieId).isEmpty()) {
            watchlistRepository.save(WatchlistMapper.toEntity(userId, movieId));
        }
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long movieId) {
        watchlistRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
}
